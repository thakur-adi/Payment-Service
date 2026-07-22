package dev.aditya.paymentservice.Repository;

import dev.aditya.paymentservice.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {
    @Override
    Optional<User> findById(Long id);

    Optional<User> findUserByStripeCustomerId(String stripeCustomerId);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByPhoneNumber(String phoneNumber);
}
