package dev.aditya.paymentservice.Repository;

import dev.aditya.paymentservice.Model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepo extends JpaRepository<Card,Long> {
    @Override
    Optional<Card> findById(Long id);

    void deleteByIdAndUserId(Long id, Long userId);

}
