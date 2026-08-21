package com.juanbonatto.fintrack.service;

import com.juanbonatto.fintrack.dto.request.TransactionRequest;
import com.juanbonatto.fintrack.dto.response.TransactionResponse;
import com.juanbonatto.fintrack.exception.ResourceNotFoundException;
import com.juanbonatto.fintrack.model.Account;
import com.juanbonatto.fintrack.model.Category;
import com.juanbonatto.fintrack.model.Transaction;
import com.juanbonatto.fintrack.model.TransactionType;
import com.juanbonatto.fintrack.model.User;
import com.juanbonatto.fintrack.repository.AccountRepository;
import com.juanbonatto.fintrack.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final CategoryService categoryService;

    public List<TransactionResponse> getAllForAccount(Long accountId, User user) {
        accountService.getOwnedAccount(accountId, user);
        return transactionRepository.findByAccountId(accountId).stream()
                .map(this::toResponse)
                .toList();
    }

    public TransactionResponse getById(Long id, User user) {
        Transaction transaction = findEntity(id);
        accountService.getOwnedAccount(transaction.getAccount().getId(), user);
        return toResponse(transaction);
    }

    @Transactional
    public TransactionResponse create(TransactionRequest request, User user) {
        Account account = accountService.getOwnedAccount(request.getAccountId(), user);
        Category category = categoryService.findEntity(request.getCategoryId());

        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .description(request.getDescription())
                .date(request.getDate())
                .type(request.getType())
                .account(account)
                .category(category)
                .build();

        applyDelta(account, request.getType(), request.getAmount());
        accountRepository.save(account);

        return toResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse update(Long id, TransactionRequest request, User user) {
        Transaction transaction = findEntity(id);
        Account currentAccount = accountService.getOwnedAccount(transaction.getAccount().getId(), user);
        Account newAccount = accountService.getOwnedAccount(request.getAccountId(), user);
        Category category = categoryService.findEntity(request.getCategoryId());

        revertDelta(currentAccount, transaction.getType(), transaction.getAmount());
        if (!currentAccount.getId().equals(newAccount.getId())) {
            accountRepository.save(currentAccount);
        }
        applyDelta(newAccount, request.getType(), request.getAmount());
        accountRepository.save(newAccount);

        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setDate(request.getDate());
        transaction.setType(request.getType());
        transaction.setAccount(newAccount);
        transaction.setCategory(category);

        return toResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public void delete(Long id, User user) {
        Transaction transaction = findEntity(id);
        Account account = accountService.getOwnedAccount(transaction.getAccount().getId(), user);

        revertDelta(account, transaction.getType(), transaction.getAmount());
        accountRepository.save(account);

        transactionRepository.delete(transaction);
    }

    private void applyDelta(Account account, TransactionType type, BigDecimal amount) {
        BigDecimal delta = type == TransactionType.INCOME ? amount : amount.negate();
        account.setBalance(account.getBalance().add(delta));
    }

    private void revertDelta(Account account, TransactionType type, BigDecimal amount) {
        BigDecimal delta = type == TransactionType.INCOME ? amount.negate() : amount;
        account.setBalance(account.getBalance().add(delta));
    }

    private Transaction findEntity(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción", id));
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .date(transaction.getDate())
                .type(transaction.getType())
                .accountId(transaction.getAccount().getId())
                .categoryId(transaction.getCategory().getId())
                .categoryName(transaction.getCategory().getName())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
