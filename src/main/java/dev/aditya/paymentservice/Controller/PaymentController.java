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

    @PostMapping("/payment-link")
    public ResponseEntity<String> generatePaymentLink(@RequestBody PaymentRequestDto paymentRequestDto){
        String url = paymentService.generatePaymentLink(paymentRequestDto.getOrderId(),paymentRequestDto.getAmount()
                                            ,paymentRequestDto.getUserId(),paymentRequestDto.getUsername()
                                            ,paymentRequestDto.getPhoneNumber(),paymentRequestDto.getUserEmail());

        return new ResponseEntity<>(url,HttpStatus.OK);
    }

    @PostMapping("/card")
    public ResponseEntity<String> saveCardDetails(@RequestBody CardDetailsDto cardDetailsDto){

        paymentService.addNewCard(cardDetailsDto.getCardHolderName(), cardDetailsDto.getCardNumber()
                                 , cardDetailsDto.getExpiryMonth(),cardDetailsDto.getExpiryYear()
                                 , cardDetailsDto.getCardNickName(), cardDetailsDto.getCardType());

        return new ResponseEntity<>("card has been added to your account", HttpStatus.CREATED);
    }

    @PutMapping("/card/{cardId}")
    public ResponseEntity<String> updateCardDetails(@PathVariable("cardId") long cardId,  @RequestBody CardDetailsDto cardDetailsDto){
        return new ResponseEntity<>("card updated", HttpStatus.OK);
    }

    @DeleteMapping("/card/{cardId}/{userId}")
    public ResponseEntity<String> deleteCardDetails(@PathVariable("cardId") long cardId,@PathVariable("userId") long userId  ){
        return new ResponseEntity<>("card deleted", HttpStatus.OK);
    }
}
