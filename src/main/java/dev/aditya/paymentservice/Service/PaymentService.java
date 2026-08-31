package dev.aditya.paymentservice.Service;


import dev.aditya.paymentservice.Dto.PaymentResponseDto;
import dev.aditya.paymentservice.Model.*;
import dev.aditya.paymentservice.Repository.CardRepo;
import dev.aditya.paymentservice.Repository.TransactionRepo;
import dev.aditya.paymentservice.Strategy.IPaymentGateway;
import dev.aditya.paymentservice.Strategy.PaymentGatewaySelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class PaymentService implements IPaymentService {

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private CardRepo cardRepo;

    @Autowired
    PaymentGatewaySelector paymentGatewaySelector;
    @Autowired
    @Qualifier("LoadBalancedRestTemplate")
    private RestTemplate restTemplate;


    @Override
    public String generatePaymentLink(long orderId, long amount, long userId,
                                      String username, String phoneNumber, String email) {
        IPaymentGateway paymentGateway = paymentGatewaySelector.selectPaymentGateway("Stripe"); //hardcoded for now

        return paymentGateway.generatePaymentLink(orderId,amount,userId,username,phoneNumber,email);
    }

    @Override
    public void addNewCard(String cardHolderName, String cardNumber, String expiryMonth
                            , String expiryYear, String cardNickName, String cardType, long userId) {

        Card card = createNewCard(userId, cardHolderName, cardNumber, expiryMonth, expiryYear, cardNickName, cardType);
        cardRepo.save(card);
    }

    @Override
    public void updateCard(long cardId, String cardHolderName, String cardNumber, String expiryMonth,
                           String expiryYear, String cardNickName, String cardType, long userId) {
        Card newCard = createNewCard(userId,cardHolderName, cardNumber, expiryMonth, expiryYear, cardNickName, cardType);
        newCard.setId(cardId);
        cardRepo.save(newCard);
    }

    @Override
    public void deleteCard(long cardId, long userId) {
        cardRepo.deleteByIdAndUserId(cardId,userId);
    }

    @Override
    public void saveTransactionDetails(String transactionId, String orderId, long amount, String paymentStatus, String paymentMethod, String userId, String paymentGateway) {
            //Always create new Transaction, it gives us details about each transaction in details like when the last update happened, what was the update etc. Each step gets recorded.
            Transaction transaction = new Transaction();
            transaction.setTransactionId(transactionId);
            transaction.setOrderId(Long.parseLong(orderId));
            transaction.setAmount(amount);
            transaction.setPaymentStatus(convertPaymentStatusToEnum(paymentStatus));
            transaction.setPaymentMethodType(convertPaymentMethodToEnum(paymentMethod));
            transaction.setUserId(Long.parseLong(userId));
            transaction.setPaymentGateway(paymentGateway);
            transactionRepo.save(transaction);

            //Update the same in orderDetails
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            PaymentResponseDto paymentResponseDto = new PaymentResponseDto();
            paymentResponseDto.setOrderId(Long.valueOf(orderId));
            paymentResponseDto.setPaymentMethod(paymentMethod);
            paymentResponseDto.setPaymentId(Long.valueOf(transactionId));
            paymentResponseDto.setPaymentStatus(paymentStatus);

            HttpEntity<PaymentResponseDto> requestEntity = new HttpEntity<>(paymentResponseDto,headers);

            ResponseEntity<String> responseEntityFromOrderSer = restTemplate.postForEntity("http://localhost:8085/order/status",requestEntity, String.class);

    }



    //Helper methods
    private PaymentMethodType convertPaymentMethodToEnum(String paymentMethodType) {
    return PaymentMethodType.CARD; //hard coded for now
    }

    private PaymentStatus convertPaymentStatusToEnum(String paymentStatus) {
        return  PaymentStatus.PROCESSING; //hard coded for now
    }

    private Card createNewCard(Long userId, String cardHolderName, String cardNumber, String expiryMonth, String expiryYear, String cardNickName, String cardType) {
    Card card = new Card();
    card.setUserId(userId);
    card.setCardHolderName(cardHolderName);
    card.setExpiryMonth(expiryMonth);
    card.setExpiryYear(expiryYear);
    card.setCardNickName(cardNickName);
    card.setCardType(convertCardTypeToEnum(cardType));
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
