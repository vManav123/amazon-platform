package com.stocker.deliveryservice.service;

import com.stocker.common.commands.delivery.DeliverOrderCommand;
import com.stocker.common.events.delivery.OrderDeliveredEvent;
import com.stocker.deliveryservice.domain.DeliveryStatus;
import com.stocker.deliveryservice.kafka.DeliveryEventProducer;
import com.stocker.deliveryservice.query.FindDeliveriesByStatusQuery;
import com.stocker.deliveryservice.query.FindDeliveryByOrderIdQuery;
import com.stocker.deliveryservice.query.FindDeliveryByTrackingNumberQuery;
import com.stocker.deliveryservice.query.FindDeliveryQuery;
import com.stocker.deliveryservice.repository.DeliveryRepository;
import com.stocker.deliveryservice.repository.entity.DeliveryEntity;
import com.stocker.deliveryservice.service.dto.DeliveryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final QueryGateway queryGateway;
    private final CommandGateway commandGateway;
    private final DeliveryEventProducer deliveryEventProducer;

    @Override
    public Mono<DeliveryDTO> processDelivery(String deliveryId, String dispatchId, String orderId, 
                                           String trackingNumber, String deliveryAddress, 
                                           LocalDateTime estimatedDeliveryTime) {
        log.info("Processing delivery directly: deliveryId={}, dispatchId={}, orderId={}, trackingNumber={}", 
                 deliveryId, dispatchId, orderId, trackingNumber);
        
        // Create a new delivery entity
        DeliveryEntity deliveryEntity = new DeliveryEntity();
        deliveryEntity.setDeliveryId(deliveryId);
        deliveryEntity.setDispatchId(dispatchId);
        deliveryEntity.setOrderId(orderId);
        deliveryEntity.setTrackingNumber(trackingNumber);
        deliveryEntity.setDeliveryAddress(deliveryAddress);
        deliveryEntity.setStatus(DeliveryStatus.IN_TRANSIT.toString());
        deliveryEntity.setEstimatedDeliveryTime(estimatedDeliveryTime);
        deliveryEntity.setCreatedAt(Instant.now());
        deliveryEntity.setUpdatedAt(Instant.now());
        
        // Save the delivery entity
        return deliveryRepository.save(deliveryEntity)
            .flatMap(savedEntity -> {
                // Simulate delivery completion (in a real system, this would be triggered by a delivery confirmation)
                // For demo purposes, we'll immediately mark it as delivered
                savedEntity.setStatus(DeliveryStatus.DELIVERED.toString());
                savedEntity.setDeliveredAt(Instant.now());
                savedEntity.setReceiverName("Customer");
                savedEntity.setDeliveryNotes("Delivered successfully");
                savedEntity.setUpdatedAt(Instant.now());
                
                return deliveryRepository.save(savedEntity)
                    .flatMap(deliveredEntity -> {
                        // Create and send the OrderDeliveredEvent
                        OrderDeliveredEvent event = OrderDeliveredEvent.builder()
                            .deliveryId(deliveredEntity.getDeliveryId())
                            .dispatchId(deliveredEntity.getDispatchId())
                            .orderId(deliveredEntity.getOrderId())
                            .trackingNumber(deliveredEntity.getTrackingNumber())
                            .status(deliveredEntity.getStatus())
                            .deliveredAt(deliveredEntity.getDeliveredAt().toString())
                            .receiverName(deliveredEntity.getReceiverName())
                            .build();
                        
                        // Send the event to Kafka
                        return deliveryEventProducer.sendOrderDeliveredEvent(event)
                            .thenReturn(mapToDTO(deliveredEntity));
                    });
            });
    }

    @Override
    public Mono<DeliveryDTO> deliverOrder(String orderId, String dispatchId, String trackingNumber, 
                                        String deliveryAddress, LocalDateTime estimatedDeliveryTime) {
        log.info("Delivering order through Axon: orderId={}, dispatchId={}, trackingNumber={}", 
                 orderId, dispatchId, trackingNumber);
        
        String deliveryId = UUID.randomUUID().toString();
        
        DeliverOrderCommand command = DeliverOrderCommand.builder()
            .deliveryId(deliveryId)
            .dispatchId(dispatchId)
            .orderId(orderId)
            .trackingNumber(trackingNumber)
            .deliveryAddress(deliveryAddress)
            .estimatedDeliveryTime(estimatedDeliveryTime)
            .build();
        
        return Mono.fromFuture(commandGateway.send(command))
            .then(getDelivery(deliveryId));
    }

    @Override
    public Mono<DeliveryDTO> getDelivery(String deliveryId) {
        log.info("Getting delivery: {}", deliveryId);
        return Mono.fromFuture(queryGateway.query(
                new FindDeliveryQuery(deliveryId),
                ResponseTypes.instanceOf(DeliveryEntity.class)
        )).map(this::mapToDTO);
    }

    @Override
    public Mono<DeliveryDTO> getDeliveryByOrderId(String orderId) {
        log.info("Getting delivery by order ID: {}", orderId);
        return Mono.fromFuture(queryGateway.query(
                new FindDeliveryByOrderIdQuery(orderId),
                ResponseTypes.instanceOf(DeliveryEntity.class)
        )).map(this::mapToDTO);
    }

    @Override
    public Mono<DeliveryDTO> getDeliveryByTrackingNumber(String trackingNumber) {
        log.info("Getting delivery by tracking number: {}", trackingNumber);
        return Mono.fromFuture(queryGateway.query(
                new FindDeliveryByTrackingNumberQuery(trackingNumber),
                ResponseTypes.instanceOf(DeliveryEntity.class)
        )).map(this::mapToDTO);
    }

    @Override
    public Flux<DeliveryDTO> getDeliveriesByStatus(String status) {
        log.info("Getting deliveries by status: {}", status);
        return Flux.from(queryGateway.query(
                new FindDeliveriesByStatusQuery(status),
                ResponseTypes.multipleInstancesOf(DeliveryEntity.class)
        )).map(this::mapToDTO);
    }
    
    private DeliveryDTO mapToDTO(DeliveryEntity entity) {
        return DeliveryDTO.builder()
                .deliveryId(entity.getDeliveryId())
                .dispatchId(entity.getDispatchId())
                .orderId(entity.getOrderId())
                .trackingNumber(entity.getTrackingNumber())
                .deliveryAddress(entity.getDeliveryAddress())
                .status(entity.getStatus())
                .deliveredAt(entity.getDeliveredAt())
                .receiverName(entity.getReceiverName())
                .deliveryNotes(entity.getDeliveryNotes())
                .build();
    }
}