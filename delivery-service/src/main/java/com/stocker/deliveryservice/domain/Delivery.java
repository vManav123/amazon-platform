package com.stocker.deliveryservice.domain;

import com.stocker.common.commands.delivery.DeliverOrderCommand;
import com.stocker.common.events.delivery.OrderDeliveredEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.LocalDateTime;

@Aggregate
@NoArgsConstructor
public class Delivery {

    @AggregateIdentifier
    private String deliveryId;
    private String dispatchId;
    private String orderId;
    private String trackingNumber;
    private String deliveryAddress;
    private LocalDateTime estimatedDeliveryTime;
    private DeliveryStatus status;
    private LocalDateTime deliveredAt;
    private String receiverName;
    private String deliveryNotes;

    @CommandHandler
    public Delivery(DeliverOrderCommand command) {
        // In a real implementation, we would integrate with a delivery service/courier
        // For simplicity, we'll simulate a successful delivery
        simulateDelivery();
        
        AggregateLifecycle.apply(new OrderDeliveredEvent(
                command.getDeliveryId(),
                command.getDispatchId(),
                command.getOrderId(),
                command.getTrackingNumber(),
                command.getDeliveryAddress(),
                "Customer", // Default receiver name
                "Delivered successfully", // Default delivery notes
                LocalDateTime.now()
        ));
    }

    @EventSourcingHandler
    public void on(OrderDeliveredEvent event) {
        this.deliveryId = event.getDeliveryId();
        this.dispatchId = event.getDispatchId();
        this.orderId = event.getOrderId();
        this.trackingNumber = event.getTrackingNumber();
        this.deliveryAddress = event.getDeliveryAddress();
        this.status = DeliveryStatus.DELIVERED;
        this.deliveredAt = event.getDeliveredAt();
        this.receiverName = event.getReceiverName();
        this.deliveryNotes = event.getDeliveryNotes();
    }
    
    private void simulateDelivery() {
        // Simulate delivery process
        // In a real implementation, this would involve tracking the package
        // and updating the status as it moves through the delivery process
        
        // For simplicity, we'll just assume the delivery is successful
    }
}