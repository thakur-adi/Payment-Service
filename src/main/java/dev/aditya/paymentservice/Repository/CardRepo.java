package dev.aditya.paymentservice.Repository;

import dev.aditya.paymentservice.Model.Card;
import dev.aditya.paymentservice.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepo extends JpaRepository<Card,Long> {
    @Override
    Optional<Card> findById(Long id);

    List<Card> findAllByUsers(List<User> users);
}
