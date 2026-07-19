package dev.aditya.paymentservice.Strategy;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("RazorpayPaymentGateway")
public class RazorpayPaymentGateway implements IPaymentGateway{
    @Override
    public String generatePaymentLink() {
        return "";
    }
}
