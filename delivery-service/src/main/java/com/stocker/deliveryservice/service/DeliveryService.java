package com.stocker.deliveryservice.service;

import com.stocker.deliveryservice.service.dto.DeliveryDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface DeliveryService {
    
    /**
     * Process a delivery directly (without going through Axon)
     * This method is used by the Kafka consumer to process deliveries when an order is dispatched
     * 
     * @param deliveryId the delivery ID
     * @param dispatchId the dispatch ID
     * @param orderId the order ID
     * @param trackingNumber the tracking number
     * @param deliveryAddress the delivery address
     * @param estimatedDeliveryTime the estimated delivery time
     * @return the delivery details
     */
    Mono<DeliveryDTO> processDelivery(String deliveryId, String dispatchId, String orderId, 
                                     String trackingNumber, String deliveryAddress, 
                                     LocalDateTime estimatedDeliveryTime);
    
    /**
     * Deliver an order
     * 
     * @param orderId the order ID
     * @param dispatchId the dispatch ID
     * @param trackingNumber the tracking number
     * @param deliveryAddress the delivery address
     * @param estimatedDeliveryTime the estimated delivery time
     * @return the delivery details
     */
    Mono<DeliveryDTO> deliverOrder(String orderId, String dispatchId, String trackingNumber, 
                                  String deliveryAddress, LocalDateTime estimatedDeliveryTime);
    
    /**
     * Get delivery by ID
     * 
     * @param deliveryId the delivery ID
     * @return the delivery details
     */
    Mono<DeliveryDTO> getDelivery(String deliveryId);
    
    /**
     * Get delivery by order ID
     * 
     * @param orderId the order ID
     * @return the delivery details
     */
    Mono<DeliveryDTO> getDeliveryByOrderId(String orderId);
    
    /**
     * Get delivery by tracking number
     * 
     * @param trackingNumber the tracking number
     * @return the delivery details
     */
    Mono<DeliveryDTO> getDeliveryByTrackingNumber(String trackingNumber);
    
    /**
     * Get deliveries by status
     * 
     * @param status the status
     * @return the delivery details
     */
    Flux<DeliveryDTO> getDeliveriesByStatus(String status);
}