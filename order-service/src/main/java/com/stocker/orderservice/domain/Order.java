package com.stocker.orderservice.domain;

import com.stocker.common.commands.order.CreateOrderCommand;
import com.stocker.common.commands.order.UpdateOrderStatusCommand;
import com.stocker.common.events.order.OrderCreatedEvent;
import com.stocker.common.events.order.OrderStatusUpdatedEvent;
import com.stocker.common.models.OrderItem;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Aggregate
@NoArgsConstructor
public class Order {

    @AggregateIdentifier
    private String orderId;
    private String userId;
    private String cartId;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private OrderStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    @CommandHandler
    public Order(CreateOrderCommand command) {
        AggregateLifecycle.apply(new OrderCreatedEvent(
                command.getOrderId(),
                command.getUserId(),
                command.getCartId(),
                command.getItems(),
                command.getTotalAmount(),
                command.getShippingAddress(),
                OrderStatus.CREATED.toString(),
                Instant.now()
        ));
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.orderId = event.getOrderId();
        this.userId = event.getUserId();
        this.cartId = event.getCartId();
        this.items = new ArrayList<>(event.getItems());
        this.totalAmount = event.getTotalAmount();
        this.shippingAddress = event.getShippingAddress();
        this.status = OrderStatus.valueOf(event.getStatus());
        this.createdAt = event.getCreatedAt();
        this.updatedAt = event.getCreatedAt();
    }

    @CommandHandler
    public void handle(UpdateOrderStatusCommand command) {
        AggregateLifecycle.apply(new OrderStatusUpdatedEvent(
                command.getOrderId(),
                command.getStatus(),
                Instant.now()
        ));
    }

    @EventSourcingHandler
    public void on(OrderStatusUpdatedEvent event) {
        this.status = OrderStatus.valueOf(event.getStatus());
        this.updatedAt = event.getUpdatedAt();
    }
}