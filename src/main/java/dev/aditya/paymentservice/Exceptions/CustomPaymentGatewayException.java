package dev.aditya.paymentservice.Exceptions;

//This class was created so that when Stripe throws any error we can actually throw this one after catching that otherwise the method name will have to include all the exceptions each payment gateway throws.
public class CustomPaymentGatewayException extends RuntimeException{

    public CustomPaymentGatewayException(String message){
        super(message);
    }
}
