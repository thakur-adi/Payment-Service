package dev.aditya.paymentservice.Service;

import com.stripe.model.checkout.Session;
import dev.aditya.paymentservice.Model.Card;
import dev.aditya.paymentservice.Model.CardType;
import dev.aditya.paymentservice.Model.Transaction;

public interface IPaymentService {

    String generatePaymentLink(long orderId, long amount, long userId, String username, String phoneNumber, String userEmail) ;

    void addNewCard(String cardHolderName, String cardNumber, String expiryMonth, String expiryYear, String cardNickName, String cardType,long userId);

    void updateCard(long cardId, String cardHolderName, String cardNumber, String expiryMonth, String expiryYear, String cardNickName, String cardType,long userId);

    void deleteCard(long cardId,long userId);

    void saveTransactionDetails(String transactionId, String orderId,long amount,String paymentStatus, String paymentMethod, String userId, String paymentGateway);

    void saveTransactionDetailsTest(String orderId,String paymentStatus,String paymentGateway);
}
