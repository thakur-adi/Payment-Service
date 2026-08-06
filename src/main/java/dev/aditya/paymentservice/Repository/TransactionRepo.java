package dev.aditya.paymentservice.Repository;

import dev.aditya.paymentservice.Model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction,Long> {

    Optional<Transaction> findTransactionById(long id);

    List<Transaction> findAllByCreatedAt(Date createdAt);

    List<Transaction> findAllByUserId(Long userId);

    //Find all transaction created by a user in month of may
    List<Transaction> findAllByUserIdAndCreatedAtBetween(Long userId, LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);
}
