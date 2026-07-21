package dev.aditya.paymentservice.Service;


import com.stripe.model.checkout.Session;
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
    public Card addNewCard(String cardHolderName, String cardNumber, String expiryMonth
                            , String expiryYear, String cardNickName, CardType cardType) {
        return null;
    }

    @Override
    public void createNewTransaction(Session session) {
        Optional<Transaction> optionalTransaction =  transactionRepo.findById(Long.valueOf(session.getPaymentIntent()));
        if(optionalTransaction.isEmpty()){
            Transaction transaction = new Transaction();
            transaction.setId(Long.parseLong(session.getPaymentIntent()));
            transaction.setOrderId(Long.parseLong(session.getMetadata().get("order-id")));
            transaction.setAmount(session.getAmountTotal());
            transaction.setPaymentStatus(convertPaymentStatusToEnum(session.getPaymentStatus()));
            transaction.setPaymentMethodType(convertPaymentMethodToEnum(session.getPaymentMethodTypes().get(0)));
            transaction.setUser(userRepo.findUserByStripeCustomerId(session.getCustomer()).get());
            transactionRepo.save(transaction);
        }
        else{
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
}
