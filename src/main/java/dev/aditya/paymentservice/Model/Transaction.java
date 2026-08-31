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
    //This actually is Payment_Intent from Stripe(Razorpay equivalent) (not Session_Id) only generated after user makes a payment.
    // Successful or Failure doesn't matter but as soon as they hit Pay button it gets generated.
    private String transactionId;
    private PaymentStatus paymentStatus;
    // This forces JPA to save "CARD", "UPI", or "CASH" into the DB as String
    @Enumerated(EnumType.STRING)
    private PaymentMethodType paymentMethodType;
    private String paymentGateway;
}
