package dev.aditya.paymentservice.Strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentGatewaySelector {

    @Autowired
     StripePaymentGateway stripePaymentGateway;
    @Autowired
    private RazorpayPaymentGateway razorpayPaymentGateway;

    public IPaymentGateway selectPaymentGateway(String selectedPaymentGateway){
        if(selectedPaymentGateway.equalsIgnoreCase("Stripe")) {
            return stripePaymentGateway;
        }
        return razorpayPaymentGateway;
    }
}
