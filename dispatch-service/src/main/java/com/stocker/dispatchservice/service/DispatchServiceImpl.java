package com.stocker.dispatchservice.service;

import com.stocker.common.commands.dispatch.DispatchOrderCommand;
import com.stocker.common.events.dispatch.OrderDispatchedEvent;
import com.stocker.dispatchservice.domain.DispatchStatus;
import com.stocker.dispatchservice.kafka.DispatchEventProducer;
import com.stocker.dispatchservice.query.FindDispatchByOrderIdQuery;
import com.stocker.dispatchservice.query.FindDispatchByTrackingNumberQuery;
import com.stocker.dispatchservice.query.FindDispatchQuery;
import com.stocker.dispatchservice.query.FindDispatchesByCarrierQuery;
import com.stocker.dispatchservice.repository.DispatchRepository;
import com.stocker.dispatchservice.repository.entity.DispatchEntity;
import com.stocker.dispatchservice.service.dto.DispatchDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DispatchServiceImpl implements DispatchService {

    private final DispatchRepository dispatchRepository;
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private final DispatchEventProducer dispatchEventProducer;
    
    @Override
    public Mono<DispatchDTO> processDispatch(String dispatchId, String orderId, String carrier) {
        log.info("Processing dispatch directly: dispatchId={}, orderId={}, carrier={}", dispatchId, orderId, carrier);
        
        // Generate a tracking number
        String trackingNumber = "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Create a new dispatch entity
        DispatchEntity dispatchEntity = new DispatchEntity();
        dispatchEntity.setId(dispatchId);
        dispatchEntity.setOrderId(orderId);
        dispatchEntity.setStatus(DispatchStatus.DISPATCHED.toString());
        dispatchEntity.setTrackingNumber(trackingNumber);
        dispatchEntity.setCarrier(carrier);
        dispatchEntity.setDispatchedAt(Instant.now());
        dispatchEntity.setCreatedAt(Instant.now());
        dispatchEntity.setUpdatedAt(Instant.now());
        
        // Save the dispatch entity
        return dispatchRepository.save(dispatchEntity)
            .flatMap(savedEntity -> {
                // Create and send the OrderDispatchedEvent
                OrderDispatchedEvent event = OrderDispatchedEvent.builder()
                    .dispatchId(savedEntity.getId())
                    .orderId(savedEntity.getOrderId())
                    .status(savedEntity.getStatus())
                    .trackingNumber(savedEntity.getTrackingNumber())
                    .carrier(savedEntity.getCarrier())
                    .dispatchedAt(savedEntity.getDispatchedAt().toString())
                    .build();
                
                // Send the event to Kafka
                return dispatchEventProducer.sendOrderDispatchedEvent(event)
                    .thenReturn(mapToDispatchDTO(savedEntity));
            });
    }

    @Override
    public Mono<DispatchDTO> dispatchOrder(String orderId, String carrier) {
        String dispatchId = UUID.randomUUID().toString();
        
        DispatchOrderCommand command = DispatchOrderCommand.builder()
                .dispatchId(dispatchId)
                .orderId(orderId)
                .carrier(carrier)
                .build();
        
        return Mono.fromFuture(commandGateway.send(command))
                .then(Mono.defer(() -> getDispatch(dispatchId)));
    }

    @Override
    public Mono<DispatchDTO> getDispatch(String dispatchId) {
        return Mono.fromFuture(
                queryGateway.query(
                        new FindDispatchQuery(dispatchId),
                        ResponseTypes.instanceOf(DispatchEntity.class)
                )
        ).map(this::mapToDispatchDTO);
    }

    @Override
    public Mono<DispatchDTO> getDispatchByOrderId(String orderId) {
        return Mono.fromFuture(
                queryGateway.query(
                        new FindDispatchByOrderIdQuery(orderId),
                        ResponseTypes.instanceOf(DispatchEntity.class)
                )
        ).map(this::mapToDispatchDTO);
    }

    @Override
    public Mono<DispatchDTO> getDispatchByTrackingNumber(String trackingNumber) {
        return Mono.fromFuture(
                queryGateway.query(
                        new FindDispatchByTrackingNumberQuery(trackingNumber),
                        ResponseTypes.instanceOf(DispatchEntity.class)
                )
        ).map(this::mapToDispatchDTO);
    }

    @Override
    public Flux<DispatchDTO> getDispatchesByCarrier(String carrier) {
        return Flux.from(
                Mono.fromFuture(
                        queryGateway.query(
                                new FindDispatchesByCarrierQuery(carrier),
                                ResponseTypes.multipleInstancesOf(DispatchEntity.class)
                        )
                )
        ).flatMapMany(Flux::fromIterable)
         .map(this::mapToDispatchDTO);
    }
    
    private DispatchDTO mapToDispatchDTO(DispatchEntity dispatchEntity) {
        return DispatchDTO.builder()
                .id(dispatchEntity.getId())
                .orderId(dispatchEntity.getOrderId())
                .status(DispatchStatus.valueOf(dispatchEntity.getStatus()))
                .trackingNumber(dispatchEntity.getTrackingNumber())
                .carrier(dispatchEntity.getCarrier())
                .dispatchedAt(dispatchEntity.getDispatchedAt())
                .createdAt(dispatchEntity.getCreatedAt())
                .updatedAt(dispatchEntity.getUpdatedAt())
                .build();
    }
}