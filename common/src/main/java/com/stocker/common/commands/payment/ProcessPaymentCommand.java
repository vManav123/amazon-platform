package com.stocker.common.commands.payment;

import com.stocker.common.models.Payment.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPaymentCommand {
    @TargetAggregateIdentifier
    private String paymentId;
    private String orderId;
    private String userId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String paymentDetails; // Could contain card details or other payment information
}