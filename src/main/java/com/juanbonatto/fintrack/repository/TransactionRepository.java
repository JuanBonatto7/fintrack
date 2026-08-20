package com.juanbonatto.fintrack.repository;

import com.juanbonatto.fintrack.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountId(Long accountId);

    List<Transaction> findByAccountIdAndDateBetween(Long accountId, LocalDate start, LocalDate end);

    List<Transaction> findByAccountIdAndCategoryId(Long accountId, Long categoryId);
}
