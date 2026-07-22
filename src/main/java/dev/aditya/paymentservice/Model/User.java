package dev.aditya.paymentservice.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class User extends Base{

    private String name;
    private String phoneNumber;
    private String email;
    private String stripeCustomerId;
    @OneToMany(mappedBy = "user")
    private List<Transaction> transactions;

    // This field overrides the class-level annotations.Rest of the fields will still get their automatic getters and setters.
    //@Getter(AccessLevel.NONE) // To avoid getters. But since it's commented out it still has a getter.
    @Setter(AccessLevel.NONE)
    @ManyToMany
    private List<Card> cards;

    public void addNewCard(Card newCard){
        cards.add(newCard);
    }
}
