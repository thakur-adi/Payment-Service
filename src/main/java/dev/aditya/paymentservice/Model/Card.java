package dev.aditya.paymentservice.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Card extends Base{

    private String cardHolderName;
    private String cardNumberBeginning;
    private String cardNumberLast;
    private String expiryMonth;
    private String expiryYear;
    private String cardNickName;
    private CardType cardType;
    @ManyToMany
    private List<User> users;
}
