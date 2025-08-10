package com.stocker.dispatchservice.service;

import com.stocker.dispatchservice.service.dto.DispatchDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DispatchService {
    
    /**
     * Process a dispatch directly (without going through Axon)
     * This method is used by the Kafka consumer to process dispatches when a payment is processed
     * 
     * @param dispatchId the dispatch ID
     * @param orderId the order ID
     * @param carrier the carrier
     * @return the dispatch details
     */
    Mono<DispatchDTO> processDispatch(String dispatchId, String orderId, String carrier);
    
    /**
     * Dispatch an order
     * 
     * @param orderId the order ID
     * @param carrier the carrier
     * @return the dispatch details
     */
    Mono<DispatchDTO> dispatchOrder(String orderId, String carrier);
    
    /**
     * Get dispatch by ID
     * 
     * @param dispatchId the dispatch ID
     * @return the dispatch details
     */
    Mono<DispatchDTO> getDispatch(String dispatchId);
    
    /**
     * Get dispatch by order ID
     * 
     * @param orderId the order ID
     * @return the dispatch details
     */
    Mono<DispatchDTO> getDispatchByOrderId(String orderId);
    
    /**
     * Get dispatch by tracking number
     * 
     * @param trackingNumber the tracking number
     * @return the dispatch details
     */
    Mono<DispatchDTO> getDispatchByTrackingNumber(String trackingNumber);
    
    /**
     * Get dispatches by carrier
     * 
     * @param carrier the carrier
     * @return the dispatch details
     */
    Flux<DispatchDTO> getDispatchesByCarrier(String carrier);
}