package dev.aditya.paymentservice.Service;

import com.stripe.model.checkout.Session;
import dev.aditya.paymentservice.Model.Card;
import dev.aditya.paymentservice.Model.CardType;
import dev.aditya.paymentservice.Model.Transaction;

public interface IPaymentService {

    String generatePaymentLink(long orderId, long amount, long userId, String username, String phoneNumber, String userEmail) ;

    Card addNewCard(String cardHolderName, String cardNumber, String expiryMonth, String expiryYear, String cardNickName, CardType cardType);

    void createNewTransaction(Session session);
}
