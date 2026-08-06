package dev.aditya.paymentservice.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Transaction extends Base{
    private long userId;
    private long orderId;
    private long amount;
    private String transactionId;
    private PaymentStatus paymentStatus;
    // This forces JPA to save "CARD", "UPI", or "CASH" into the DB as String
    @Enumerated(EnumType.STRING)
    private PaymentMethodType paymentMethodType;
    private String paymentGateway;
}
