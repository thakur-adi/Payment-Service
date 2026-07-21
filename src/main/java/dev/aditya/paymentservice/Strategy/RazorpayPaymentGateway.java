package dev.aditya.paymentservice.Strategy;

import dev.aditya.paymentservice.Model.Transaction;
import dev.aditya.paymentservice.Model.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("RazorpayPaymentGateway")
public class RazorpayPaymentGateway implements IPaymentGateway{
    @Override
    public String generatePaymentLink(long orderId, long amount, User user) {
        return null;
    }
}
