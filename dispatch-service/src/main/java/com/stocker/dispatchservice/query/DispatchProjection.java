package com.stocker.dispatchservice.query;

import com.stocker.common.events.dispatch.OrderDispatchedEvent;
import com.stocker.dispatchservice.domain.DispatchStatus;
import com.stocker.dispatchservice.repository.DispatchRepository;
import com.stocker.dispatchservice.repository.entity.DispatchEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class DispatchProjection {

    private final DispatchRepository dispatchRepository;

    @EventHandler
    public void on(OrderDispatchedEvent event) {
        log.info("Handling OrderDispatchedEvent: {}", event.getDispatchId());
        
        DispatchEntity dispatchEntity = DispatchEntity.builder()
                .id(event.getDispatchId())
                .orderId(event.getOrderId())
                .status(event.getStatus())
                .trackingNumber(event.getTrackingNumber())
                .carrier(event.getCarrier())
                .dispatchedAt(event.getDispatchedAt())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        
        dispatchRepository.save(dispatchEntity).subscribe(
            savedDispatch -> log.info("Dispatch saved: {}", savedDispatch.getId()),
            error -> log.error("Error saving dispatch: {}", error.getMessage())
        );
    }

    @QueryHandler
    public Mono<DispatchEntity> findDispatch(FindDispatchQuery query) {
        log.info("Handling FindDispatchQuery: {}", query.getDispatchId());
        return dispatchRepository.findById(query.getDispatchId());
    }

    @QueryHandler
    public Mono<DispatchEntity> findDispatchByOrderId(FindDispatchByOrderIdQuery query) {
        log.info("Handling FindDispatchByOrderIdQuery: {}", query.getOrderId());
        return dispatchRepository.findByOrderId(query.getOrderId());
    }

    @QueryHandler
    public Mono<DispatchEntity> findDispatchByTrackingNumber(FindDispatchByTrackingNumberQuery query) {
        log.info("Handling FindDispatchByTrackingNumberQuery: {}", query.getTrackingNumber());
        return dispatchRepository.findByTrackingNumber(query.getTrackingNumber());
    }

    @QueryHandler
    public Flux<DispatchEntity> findDispatchesByCarrier(FindDispatchesByCarrierQuery query) {
        log.info("Handling FindDispatchesByCarrierQuery: {}", query.getCarrier());
        return dispatchRepository.findByCarrier(query.getCarrier());
    }
}