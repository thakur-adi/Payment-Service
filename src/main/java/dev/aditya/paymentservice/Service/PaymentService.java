package dev.aditya.paymentservice.Service;


import com.stripe.model.checkout.Session;
import dev.aditya.paymentservice.Exceptions.UserNotFoundException;
import dev.aditya.paymentservice.Model.*;
import dev.aditya.paymentservice.Repository.CardRepo;
import dev.aditya.paymentservice.Repository.TransactionRepo;
import dev.aditya.paymentservice.Repository.UserRepo;
import dev.aditya.paymentservice.Strategy.IPaymentGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentService implements IPaymentService {
    @Autowired
    @Qualifier("StripePaymentGateway")
    private IPaymentGateway paymentGateway;

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private CardRepo cardRepo;

    @Autowired
    private UserRepo userRepo;


    @Override
    public String generatePaymentLink(long orderId, long amount, long userId,
                                      String username, String phoneNumber, String email) {
        Optional<User> optionalUser = userRepo.findById(userId);
        User user;
        if(optionalUser.isEmpty()){
             user = new User();
             user.setId(userId);
             user.setName(username);
             user.setEmail(email);
             user.setPhoneNumber(phoneNumber);
             userRepo.save(user);
        }
        else {user = optionalUser.get();}


        return paymentGateway.generatePaymentLink(orderId,amount,user);
    }

    @Override
    public void addNewCard(String cardHolderName, String cardNumber, String expiryMonth
                            , String expiryYear, String cardNickName, String cardType, Long userId) {

        Card card = createNewCard(cardHolderName, cardNumber, expiryMonth, expiryYear, cardNickName, cardType);
        cardRepo.save(card);
        if(userRepo.findById(userId).isEmpty()){
            throw new UserNotFoundException("Please provide correct userId");
        }
        User user = userRepo.findById(userId).get();
        user.addNewCard(card);
        userRepo.save(user);
    }

    @Override
    public void updateCard(long cardId, String cardHolderName, String cardNumber, String expiryMonth, String expiryYear, String cardNickName, String cardType, long userId) {
        Card newCard = createNewCard(cardHolderName, cardNumber, expiryMonth, expiryYear, cardNickName, cardType);
        newCard.setId(cardId);
        if(userRepo.findById(userId).isEmpty()){
            throw new UserNotFoundException("Please provide correct userId");
        }
        User user = userRepo.findById(userId).get();
        user.getCards().removeIf(oldCard -> oldCard.getId()==cardId);
        user.addNewCard(newCard);
        userRepo.save(user);
        cardRepo.deleteById(cardId);
        cardRepo.save(newCard);
    }

    @Override
    public void deleteCard(long cardId, long userId) {
        if(userRepo.findById(userId).isEmpty()){
            throw new UserNotFoundException("Please provide correct userId");
        }
        User user = userRepo.findById(userId).get();
        user.getCards().removeIf(oldCard -> oldCard.getId()==cardId);
        userRepo.save(user);
        cardRepo.deleteById(cardId);
    }


    @Override
    public void updateTansactionDetails(Session session) {
        Optional<Transaction> optionalTransaction =  transactionRepo.findById(Long.valueOf(session.getPaymentIntent()));
        if(optionalTransaction.isEmpty()){
            //create new Transaction
            Transaction transaction = new Transaction();
            transaction.setId(Long.parseLong(session.getPaymentIntent()));
            transaction.setOrderId(Long.parseLong(session.getMetadata().get("order-id")));
            transaction.setAmount(session.getAmountTotal());
            transaction.setPaymentStatus(convertPaymentStatusToEnum(session.getPaymentStatus()));
            transaction.setPaymentMethodType(convertPaymentMethodToEnum(session.getPaymentMethodTypes().get(0)));
            if(userRepo.findUserByStripeCustomerId(session.getCustomer()).isEmpty()){
               Optional<User> userOptional =  userRepo.findById(Long.valueOf(session.getClientReferenceId()));
               User user = userOptional.get();
               user.setStripeCustomerId(session.getCustomer());
               userRepo.save(user);
            }
            transaction.setUser(userRepo.findById(Long.valueOf(session.getClientReferenceId())).get());
            transactionRepo.save(transaction);
        }
        else{
            //update existing transaction
            Transaction transaction = optionalTransaction.get();
            transaction.setPaymentStatus(convertPaymentStatusToEnum(session.getPaymentStatus()));
            transactionRepo.save(transaction);
        }
    }



    //Helper methods
    private PaymentMethodType convertPaymentMethodToEnum(String paymentMethodType) {
    return PaymentMethodType.CARD;
    }

    private PaymentStatus convertPaymentStatusToEnum(String paymentStatus) {
        return  PaymentStatus.PROCESSING;
    }

    private Card createNewCard(String cardHolderName, String cardNumber, String expiryMonth, String expiryYear, String cardNickName, String cardType) {
    Card card = new Card();
    card.setCardHolderName(cardHolderName);
    card.setExpiryMonth(expiryMonth);
    card.setExpiryYear(expiryYear);
    card.setCardNickName(cardNickName);
    card.setCardType(convertCardTypeToEnum(cardType));
    card.setCardNumberBeginning(cardNumber.substring(0,8));
    card.setCardNumberLast(cardNumber.substring(8));
    return card;
    }

    private CardType convertCardTypeToEnum(String cardType) {
        if(cardType.equalsIgnoreCase("Debit Card")){
            return CardType.DEBIT_CARD;
        }
        else if(cardType.equalsIgnoreCase("Credit Card")){
            return CardType.CREDIT_CARD;
        }
        else {
            throw new RuntimeException("Please enter proper CardType");
        }

    }
}
