package dev.aditya.paymentservice.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Transaction extends Base{
    private long orderId;
    private long amount;
    private PaymentStatus paymentStatus;
    // This forces JPA to save "CARD", "UPI", or "CASH" into the DB
    @Enumerated(EnumType.STRING)
    private PaymentMethodType paymentMethodType;
    @ManyToOne
    private User user;

}
