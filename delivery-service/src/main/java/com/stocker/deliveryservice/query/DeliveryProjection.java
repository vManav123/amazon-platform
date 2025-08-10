package com.stocker.deliveryservice.query;

import com.stocker.common.events.delivery.OrderDeliveredEvent;
import com.stocker.deliveryservice.domain.DeliveryStatus;
import com.stocker.deliveryservice.repository.DeliveryRepository;
import com.stocker.deliveryservice.repository.entity.DeliveryEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryProjection {

    private final DeliveryRepository deliveryRepository;

    @EventHandler
    public void on(OrderDeliveredEvent event) {
        log.info("Handling OrderDeliveredEvent: {}", event.getDeliveryId());
        
        DeliveryEntity deliveryEntity = new DeliveryEntity();
        deliveryEntity.setDeliveryId(event.getDeliveryId());
        deliveryEntity.setDispatchId(event.getDispatchId());
        deliveryEntity.setOrderId(event.getOrderId());
        deliveryEntity.setTrackingNumber(event.getTrackingNumber());
        deliveryEntity.setDeliveryAddress(event.getDeliveryAddress());
        deliveryEntity.setStatus(DeliveryStatus.DELIVERED.toString());
        deliveryEntity.setDeliveredAt(event.getDeliveredAt());
        deliveryEntity.setReceiverName(event.getReceiverName());
        deliveryEntity.setDeliveryNotes(event.getDeliveryNotes());
        
        deliveryRepository.save(deliveryEntity).subscribe(
            savedDelivery -> log.info("Delivery saved: {}", savedDelivery.getDeliveryId()),
            error -> log.error("Error saving delivery: {}", error.getMessage())
        );
    }

    @QueryHandler
    public Mono<DeliveryEntity> findDelivery(FindDeliveryQuery query) {
        log.info("Handling FindDeliveryQuery: {}", query.getDeliveryId());
        return deliveryRepository.findById(query.getDeliveryId());
    }

    @QueryHandler
    public Mono<DeliveryEntity> findDeliveryByOrderId(FindDeliveryByOrderIdQuery query) {
        log.info("Handling FindDeliveryByOrderIdQuery: {}", query.getOrderId());
        return deliveryRepository.findByOrderId(query.getOrderId());
    }

    @QueryHandler
    public Mono<DeliveryEntity> findDeliveryByTrackingNumber(FindDeliveryByTrackingNumberQuery query) {
        log.info("Handling FindDeliveryByTrackingNumberQuery: {}", query.getTrackingNumber());
        return deliveryRepository.findByTrackingNumber(query.getTrackingNumber());
    }

    @QueryHandler
    public Flux<DeliveryEntity> findDeliveriesByStatus(FindDeliveriesByStatusQuery query) {
        log.info("Handling FindDeliveriesByStatusQuery: {}", query.getStatus());
        return deliveryRepository.findByStatus(query.getStatus());
    }
}