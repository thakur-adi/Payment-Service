package dev.aditya.paymentservice.Strategy;

import org.springframework.stereotype.Component;

@Component
public class PaymentGatewaySelector {

    public static IPaymentGateway selectPaymentGateway(String selectedPaymentGateway){
        return null;
    }
}
