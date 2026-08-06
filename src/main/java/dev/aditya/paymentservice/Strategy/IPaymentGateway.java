package dev.aditya.paymentservice.Strategy;

public interface IPaymentGateway {

    String generatePaymentLink(long orderId, long amount, Long userId, String userName , String phoneNumber, String userEmail);
}
