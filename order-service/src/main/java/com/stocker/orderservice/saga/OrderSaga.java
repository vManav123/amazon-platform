package com.stocker.orderservice.saga;

import com.stocker.common.commands.delivery.DeliverOrderCommand;
import com.stocker.common.commands.dispatch.DispatchOrderCommand;
import com.stocker.common.commands.order.UpdateOrderStatusCommand;
import com.stocker.common.commands.payment.ProcessPaymentCommand;
import com.stocker.common.events.delivery.OrderDeliveredEvent;
import com.stocker.common.events.dispatch.OrderDispatchedEvent;
import com.stocker.common.events.order.OrderCreatedEvent;
import com.stocker.common.events.payment.PaymentProcessedEvent;
import com.stocker.orderservice.domain.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;

@Saga
@Slf4j
public class OrderSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent event) {
        log.info("Starting saga for order: {}", event.getOrderId());
        
        // Associate the saga with the payment ID to correlate future events
        String paymentId = UUID.randomUUID().toString();
        SagaLifecycle.associateWith("paymentId", paymentId);
        
        // Process payment
        ProcessPaymentCommand paymentCommand = ProcessPaymentCommand.builder()
                .paymentId(paymentId)
                .orderId(event.getOrderId())
                .amount(event.getTotalAmount())
                .paymentMethod("CREDIT_CARD") // Default payment method
                .build();
        
        log.info("Sending ProcessPaymentCommand: {}", paymentCommand);
        commandGateway.send(paymentCommand)
            .exceptionally(ex -> {
                log.error("Error processing payment: {}", ex.getMessage());
                // Compensating transaction
                commandGateway.send(new UpdateOrderStatusCommand(
                        event.getOrderId(),
                        OrderStatus.PAYMENT_FAILED.toString()
                ));
                return null;
            });
    }
    
    @SagaEventHandler(associationProperty = "paymentId")
    public void handle(PaymentProcessedEvent event) {
        log.info("Handling PaymentProcessedEvent: {}", event.getPaymentId());
        
        // Update order status based on payment result
        if ("COMPLETED".equals(event.getStatus())) {
            try {
                // Payment successful, update order status
                commandGateway.send(new UpdateOrderStatusCommand(
                        event.getOrderId(),
                        OrderStatus.PAID.toString()
                )).exceptionally(ex -> {
                    log.error("Error updating order status to PAID: {}", ex.getMessage());
                    return null;
                });
                
                // Proceed with dispatch
                String dispatchId = UUID.randomUUID().toString();
                SagaLifecycle.associateWith("dispatchId", dispatchId);
                
                DispatchOrderCommand dispatchCommand = DispatchOrderCommand.builder()
                        .dispatchId(dispatchId)
                        .orderId(event.getOrderId())
                        .carrier("DEFAULT_CARRIER")
                        .build();
                
                log.info("Sending DispatchOrderCommand: {}", dispatchCommand);
                commandGateway.send(dispatchCommand)
                    .exceptionally(ex -> {
                        log.error("Error dispatching order: {}", ex.getMessage());
                        // Compensating transaction - mark order as payment-only
                        commandGateway.send(new UpdateOrderStatusCommand(
                                event.getOrderId(),
                                OrderStatus.DISPATCH_FAILED.toString()
                        ));
                        return null;
                    });
            } catch (Exception ex) {
                log.error("Error in payment processing saga: {}", ex.getMessage());
                // Compensating transaction
                commandGateway.send(new UpdateOrderStatusCommand(
                        event.getOrderId(),
                        OrderStatus.ERROR.toString()
                )).exceptionally(ex -> {
                    log.error("Error updating order status to ERROR: {}", ex.getMessage());
                    return null;
                });
                SagaLifecycle.end();
            }
        } else {
            // Payment failed, update order status and end saga
            commandGateway.send(new UpdateOrderStatusCommand(
                    event.getOrderId(),
                    OrderStatus.PAYMENT_FAILED.toString()
            )).exceptionally(ex -> {
                log.error("Error updating order status to PAYMENT_FAILED: {}", ex.getMessage());
                return null;
            });
            
            SagaLifecycle.end();
        }
    }
    
    @SagaEventHandler(associationProperty = "dispatchId")
    public void handle(OrderDispatchedEvent event) {
        log.info("Handling OrderDispatchedEvent: {}", event.getDispatchId());
        
        try {
            // Update order status
            commandGateway.send(new UpdateOrderStatusCommand(
                    event.getOrderId(),
                    OrderStatus.DISPATCHED.toString()
            )).exceptionally(ex -> {
                log.error("Error updating order status to DISPATCHED: {}", ex.getMessage());
                return null;
            });
            
            // Proceed with delivery
            String deliveryId = UUID.randomUUID().toString();
            SagaLifecycle.associateWith("deliveryId", deliveryId);
            
            DeliverOrderCommand deliverCommand = DeliverOrderCommand.builder()
                    .deliveryId(deliveryId)
                    .dispatchId(event.getDispatchId())
                    .orderId(event.getOrderId())
                    .trackingNumber(event.getTrackingNumber())
                    .deliveryAddress("Customer Address") // This should come from the order
                    .estimatedDeliveryTime(LocalDateTime.now().plusDays(3))
                    .build();
            
            log.info("Sending DeliverOrderCommand: {}", deliverCommand);
            commandGateway.send(deliverCommand)
                .exceptionally(ex -> {
                    log.error("Error delivering order: {}", ex.getMessage());
                    // Compensating transaction - mark order as dispatched-only
                    commandGateway.send(new UpdateOrderStatusCommand(
                            event.getOrderId(),
                            OrderStatus.DELIVERY_FAILED.toString()
                    ));
                    return null;
                });
        } catch (Exception ex) {
            log.error("Error in dispatch processing saga: {}", ex.getMessage());
            // Compensating transaction
            commandGateway.send(new UpdateOrderStatusCommand(
                    event.getOrderId(),
                    OrderStatus.ERROR.toString()
            )).exceptionally(ex -> {
                log.error("Error updating order status to ERROR during dispatch: {}", ex.getMessage());
                return null;
            });
            SagaLifecycle.end();
        }
    }
    
    @EndSaga
    @SagaEventHandler(associationProperty = "deliveryId")
    public void handle(OrderDeliveredEvent event) {
        log.info("Handling OrderDeliveredEvent: {}", event.getDeliveryId());
        
        try {
            // Update order status to delivered
            commandGateway.send(new UpdateOrderStatusCommand(
                    event.getOrderId(),
                    OrderStatus.DELIVERED.toString()
            )).exceptionally(ex -> {
                log.error("Error updating order status to delivered: {}", ex.getMessage());
                return null;
            });
            
            log.info("Order saga completed for order: {}", event.getOrderId());
        } catch (Exception ex) {
            log.error("Error in delivery processing saga: {}", ex.getMessage());
            // Even if there's an error, we end the saga as this is the final step
            // But we should log the error for monitoring
            log.error("Order saga completed with errors for order: {}", event.getOrderId());
        }
    }
}