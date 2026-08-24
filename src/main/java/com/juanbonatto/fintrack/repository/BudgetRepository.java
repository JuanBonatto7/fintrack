package com.juanbonatto.fintrack.repository;

import com.juanbonatto.fintrack.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByAccountId(Long accountId);

    List<Budget> findByAccountIdAndYearAndMonth(Long accountId, Integer year, Integer month);

    Optional<Budget> findByAccountIdAndCategoryIdAndYearAndMonth(Long accountId, Long categoryId, Integer year, Integer month);
}
