package dev.aditya.paymentservice.Repository;

import dev.aditya.paymentservice.Model.Transaction;
import dev.aditya.paymentservice.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction,Long> {

    Optional<Transaction> findTransactionById(long id);

    List<Transaction> findAllByCreatedAt(Date createdAt);
    
    List<Transaction> findAllByUser_Id(long userId);
    
    List<Transaction> findAllByUserAndCreatedAtBetween(User user, Date createdAtAfter, Date createdAtBefore);
}
