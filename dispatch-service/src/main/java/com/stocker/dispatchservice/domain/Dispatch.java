package com.stocker.dispatchservice.domain;

import com.stocker.common.commands.dispatch.DispatchOrderCommand;
import com.stocker.common.events.dispatch.OrderDispatchedEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.Instant;
import java.util.Random;

@Aggregate
@NoArgsConstructor
@Slf4j
public class Dispatch {

    @AggregateIdentifier
    private String dispatchId;
    private String orderId;
    private DispatchStatus status;
    private String trackingNumber;
    private String carrier;
    private Instant dispatchedAt;

    @CommandHandler
    public Dispatch(DispatchOrderCommand command) {
        log.info("Processing dispatch for order: {}, carrier: {}", command.getOrderId(), command.getCarrier());
        
        try {
            // In a real implementation, we would integrate with a warehouse/logistics system
            // For simplicity, we'll simulate dispatch processing with different carriers
            DispatchResult result = processDispatch(command.getOrderId(), command.getCarrier());
            
            log.info("Order dispatched: {}, status: {}, tracking: {}", 
                    command.getOrderId(), result.getStatus(), result.getTrackingNumber());
            
            AggregateLifecycle.apply(new OrderDispatchedEvent(
                    command.getDispatchId(),
                    command.getOrderId(),
                    result.getStatus().toString(),
                    result.getTrackingNumber(),
                    command.getCarrier(),
                    Instant.now()
            ));
        } catch (Exception ex) {
            log.error("Error dispatching order: {}", command.getOrderId(), ex);
            
            // Even in case of technical errors, we need to apply an event to maintain the saga
            AggregateLifecycle.apply(new OrderDispatchedEvent(
                    command.getDispatchId(),
                    command.getOrderId(),
                    DispatchStatus.FAILED.toString(),
                    null,
                    command.getCarrier(),
                    Instant.now()
            ));
        }
    }

    @EventSourcingHandler
    public void on(OrderDispatchedEvent event) {
        this.dispatchId = event.getDispatchId();
        this.orderId = event.getOrderId();
        this.status = DispatchStatus.valueOf(event.getStatus());
        this.trackingNumber = event.getTrackingNumber();
        this.carrier = event.getCarrier();
        this.dispatchedAt = event.getDispatchedAt();
    }
    
    private DispatchResult processDispatch(String orderId, String carrier) {
        log.info("Processing dispatch for order: {}, carrier: {}", orderId, carrier);
        
        // Simulate different dispatch scenarios based on carrier
        // In a real implementation, this would call an actual logistics system
        DispatchStatus status;
        String trackingNumber = generateTrackingNumber(carrier);
        
        try {
            // Simulate different carrier behaviors
            switch (carrier.toUpperCase()) {
                case "DHL":
                    // DHL has high success rate
                    Thread.sleep(300); // Simulate processing time
                    status = Math.random() < 0.95 ? DispatchStatus.DISPATCHED : DispatchStatus.PENDING;
                    break;
                case "FEDEX":
                    // FedEx has medium success rate
                    Thread.sleep(200);
                    status = Math.random() < 0.9 ? DispatchStatus.DISPATCHED : DispatchStatus.PENDING;
                    break;
                case "UPS":
                    // UPS has lower success rate
                    Thread.sleep(250);
                    status = Math.random() < 0.85 ? DispatchStatus.DISPATCHED : DispatchStatus.PENDING;
                    break;
                default:
                    // Default carrier
                    status = Math.random() < 0.8 ? DispatchStatus.DISPATCHED : DispatchStatus.PENDING;
            }
            
            // Simulate occasional processing errors
            if (Math.random() < 0.05) {
                throw new RuntimeException("Simulated logistics system error");
            }
            
            return new DispatchResult(status, trackingNumber);
        } catch (InterruptedException e) {
            log.error("Dispatch processing interrupted", e);
            Thread.currentThread().interrupt();
            return new DispatchResult(DispatchStatus.FAILED, null);
        }
    }
    
    private String generateTrackingNumber(String carrier) {
        // Generate a carrier-specific tracking number format
        Random random = new Random();
        String prefix;
        
        switch (carrier.toUpperCase()) {
            case "DHL":
                prefix = "DHL";
                return prefix + String.format("%10d", Math.abs(random.nextLong() % 10000000000L));
            case "FEDEX":
                prefix = "FDX";
                return prefix + String.format("%12d", Math.abs(random.nextLong() % 1000000000000L));
            case "UPS":
                prefix = "1Z";
                return prefix + String.format("%16d", Math.abs(random.nextLong() % 10000000000000000L));
            default:
                prefix = "TRK";
                return prefix + System.currentTimeMillis() + random.nextInt(1000);
        }
    }
    
    @lombok.Value
    private static class DispatchResult {
        DispatchStatus status;
        String trackingNumber;
    }
}