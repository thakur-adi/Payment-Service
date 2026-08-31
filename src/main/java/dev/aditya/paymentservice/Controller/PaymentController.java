package dev.aditya.paymentservice.Controller;


import dev.aditya.paymentservice.Dto.CardDetailsDto;
import dev.aditya.paymentservice.Dto.PaymentRequestDto;
import dev.aditya.paymentservice.Service.IPaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PaymentController {

    private final IPaymentService paymentService;

    PaymentController(IPaymentService paymentService){
        this.paymentService = paymentService;
    }

    @PostMapping("/pay")
    public ResponseEntity<String> generatePaymentLink(@RequestBody PaymentRequestDto paymentRequestDto){
        /* Normal Code -> Just commented out for testing purposes
        String url = paymentService.generatePaymentLink(paymentRequestDto.getOrderId(),paymentRequestDto.getAmount()
                                            ,paymentRequestDto.getUserId(),paymentRequestDto.getUsername()
                                            ,paymentRequestDto.getPhoneNumber(),paymentRequestDto.getUserEmail());

        Just sending in Link as payment/transaction_id has not been generated just yet.
         */
        return new ResponseEntity<>("www.google.com",HttpStatus.CREATED);
    }

    @PostMapping("/card")
    public ResponseEntity<String> saveCardDetails(@RequestBody CardDetailsDto cardDetailsDto){

        paymentService.addNewCard(cardDetailsDto.getCardHolderName(), cardDetailsDto.getCardNumber()
                                 , cardDetailsDto.getExpiryMonth(),cardDetailsDto.getExpiryYear()
                                 , cardDetailsDto.getCardNickName(), cardDetailsDto.getCardType()
                                 , cardDetailsDto.getUserId());

        return new ResponseEntity<>("card has been added to your account", HttpStatus.CREATED);
    }

    @PutMapping("/card/{cardId}")
    public ResponseEntity<String> updateCardDetails(@PathVariable("cardId") long cardId,  @RequestBody CardDetailsDto cardDetailsDto){

        paymentService.updateCard(cardId,cardDetailsDto.getCardHolderName(), cardDetailsDto.getCardNumber()
                                 , cardDetailsDto.getExpiryMonth(),cardDetailsDto.getExpiryYear()
                                 , cardDetailsDto.getCardNickName(), cardDetailsDto.getCardType()
                                 , cardDetailsDto.getUserId());
        return new ResponseEntity<>("card details have been updated", HttpStatus.OK);
    }

    @DeleteMapping("/card/{cardId}/{userId}")
    public ResponseEntity<String> deleteCardDetails(@PathVariable("cardId") long cardId,@PathVariable("userId") long userId  ){
        paymentService.deleteCard(cardId,userId);
        return new ResponseEntity<>("card deleted", HttpStatus.OK);
    }
}
