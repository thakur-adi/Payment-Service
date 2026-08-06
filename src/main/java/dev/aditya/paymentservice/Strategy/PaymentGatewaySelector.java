package dev.aditya.paymentservice.Strategy;

import dev.aditya.paymentservice.Model.PaymentGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentGatewaySelector {

    @Autowired
     StripePaymentGateway stripePaymentGateway;
    @Autowired
    private RazorpayPaymentGateway razorpayPaymentGateway;

    public IPaymentGateway selectPaymentGateway(String selectedPaymentGateway){
        if(selectedPaymentGateway.equalsIgnoreCase(PaymentGateway.STRIPE.toString())) {
            return stripePaymentGateway;
        }
        return razorpayPaymentGateway;
    }
}
