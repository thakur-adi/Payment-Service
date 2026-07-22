package dev.aditya.paymentservice.Dto;

import dev.aditya.paymentservice.Model.CardType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardDetailsDto {
    private String cardHolderName;
    private String cardNumber;
    private String expiryMonth;
    private String expiryYear;
    private String cardNickName;
    private String cardType;
    private Long userId;
}
