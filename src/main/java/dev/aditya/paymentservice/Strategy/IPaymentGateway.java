package dev.aditya.paymentservice.Strategy;

import dev.aditya.paymentservice.Model.Transaction;
import dev.aditya.paymentservice.Model.User;

public interface IPaymentGateway {

    String generatePaymentLink(long orderId, long amount, User user);
}
