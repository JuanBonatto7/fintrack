package com.juanbonatto.fintrack.service;

import com.juanbonatto.fintrack.dto.request.BudgetRequest;
import com.juanbonatto.fintrack.dto.response.BudgetResponse;
import com.juanbonatto.fintrack.exception.BudgetAlreadyExistsException;
import com.juanbonatto.fintrack.exception.ResourceNotFoundException;
import com.juanbonatto.fintrack.model.Account;
import com.juanbonatto.fintrack.model.Budget;
import com.juanbonatto.fintrack.model.Category;
import com.juanbonatto.fintrack.model.User;
import com.juanbonatto.fintrack.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final AccountService accountService;
    private final CategoryService categoryService;

    public List<BudgetResponse> getAllForAccount(Long accountId, User user) {
        accountService.getOwnedAccount(accountId, user);
        return budgetRepository.findByAccountId(accountId).stream()
                .map(this::toResponse)
                .toList();
    }

    public BudgetResponse getById(Long id, User user) {
        Budget budget = findEntity(id);
        accountService.getOwnedAccount(budget.getAccount().getId(), user);
        return toResponse(budget);
    }

    public BudgetResponse create(BudgetRequest request, User user) {
        Account account = accountService.getOwnedAccount(request.getAccountId(), user);
        Category category = categoryService.findEntity(request.getCategoryId());

        budgetRepository.findByAccountIdAndCategoryIdAndYearAndMonth(
                account.getId(), category.getId(), request.getYear(), request.getMonth()
        ).ifPresent(existing -> {
            throw new BudgetAlreadyExistsException();
        });

        Budget budget = Budget.builder()
                .amount(request.getAmount())
                .month(request.getMonth())
                .year(request.getYear())
                .account(account)
                .category(category)
                .build();

        return toResponse(budgetRepository.save(budget));
    }

    public BudgetResponse update(Long id, BudgetRequest request, User user) {
        Budget budget = findEntity(id);
        accountService.getOwnedAccount(budget.getAccount().getId(), user);
        Category category = categoryService.findEntity(request.getCategoryId());

        budgetRepository.findByAccountIdAndCategoryIdAndYearAndMonth(
                budget.getAccount().getId(), category.getId(), request.getYear(), request.getMonth()
        ).ifPresent(existing -> {
            if (!existing.getId().equals(budget.getId())) {
                throw new BudgetAlreadyExistsException();
            }
        });

        budget.setAmount(request.getAmount());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());
        budget.setCategory(category);

        return toResponse(budgetRepository.save(budget));
    }

    public void delete(Long id, User user) {
        Budget budget = findEntity(id);
        accountService.getOwnedAccount(budget.getAccount().getId(), user);
        budgetRepository.delete(budget);
    }

    private Budget findEntity(Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto", id));
    }

    private BudgetResponse toResponse(Budget budget) {
        return BudgetResponse.builder()
                .id(budget.getId())
                .amount(budget.getAmount())
                .month(budget.getMonth())
                .year(budget.getYear())
                .accountId(budget.getAccount().getId())
                .categoryId(budget.getCategory().getId())
                .categoryName(budget.getCategory().getName())
                .build();
    }
}
