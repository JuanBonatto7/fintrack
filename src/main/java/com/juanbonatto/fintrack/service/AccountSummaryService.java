package com.juanbonatto.fintrack.service;

import com.juanbonatto.fintrack.dto.response.AccountSummaryResponse;
import com.juanbonatto.fintrack.dto.response.CategoryBudgetSummary;
import com.juanbonatto.fintrack.model.Account;
import com.juanbonatto.fintrack.model.Budget;
import com.juanbonatto.fintrack.model.Transaction;
import com.juanbonatto.fintrack.model.TransactionType;
import com.juanbonatto.fintrack.model.User;
import com.juanbonatto.fintrack.repository.BudgetRepository;
import com.juanbonatto.fintrack.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountSummaryService {

    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    public AccountSummaryResponse getSummary(Long accountId, Integer year, Integer month, User user) {
        Account account = accountService.getOwnedAccount(accountId, user);

        YearMonth period = (year != null && month != null)
                ? YearMonth.of(year, month)
                : YearMonth.now();

        LocalDate start = period.atDay(1);
        LocalDate end = period.atEndOfMonth();

        List<Transaction> transactions = transactionRepository.findByAccountIdAndDateBetween(accountId, start, end);

        BigDecimal totalIncome = sumByType(transactions, TransactionType.INCOME);
        BigDecimal totalExpense = sumByType(transactions, TransactionType.EXPENSE);

        List<Budget> budgets = budgetRepository.findByAccountIdAndYearAndMonth(
                accountId, period.getYear(), period.getMonthValue()
        );

        List<CategoryBudgetSummary> categorySummaries = budgets.stream()
                .map(budget -> toCategorySummary(budget, transactions))
                .toList();

        return AccountSummaryResponse.builder()
                .accountId(account.getId())
                .year(period.getYear())
                .month(period.getMonthValue())
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .net(totalIncome.subtract(totalExpense))
                .balance(account.getBalance())
                .categorySummaries(categorySummaries)
                .build();
    }

    private CategoryBudgetSummary toCategorySummary(Budget budget, List<Transaction> transactions) {
        BigDecimal spent = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .filter(t -> t.getCategory().getId().equals(budget.getCategory().getId()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CategoryBudgetSummary.builder()
                .categoryId(budget.getCategory().getId())
                .categoryName(budget.getCategory().getName())
                .budgeted(budget.getAmount())
                .spent(spent)
                .remaining(budget.getAmount().subtract(spent))
                .build();
    }

    private BigDecimal sumByType(List<Transaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
