package dev.aditya.paymentservice.Model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Card extends Base{
    private Long userId;
    private String cardHolderName;
    private String cardNumberLast;
    private String expiryMonth;
    private String expiryYear;
    private String cardNickName;
    private CardType cardType;
}
