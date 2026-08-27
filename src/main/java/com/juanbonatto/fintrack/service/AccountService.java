package com.juanbonatto.fintrack.service;

import com.juanbonatto.fintrack.dto.request.AccountRequest;
import com.juanbonatto.fintrack.dto.response.AccountBalancesResponse;
import com.juanbonatto.fintrack.dto.response.AccountResponse;
import com.juanbonatto.fintrack.exception.AccessDeniedException;
import com.juanbonatto.fintrack.exception.ResourceNotFoundException;
import com.juanbonatto.fintrack.model.Account;
import com.juanbonatto.fintrack.model.Currency;
import com.juanbonatto.fintrack.model.User;
import com.juanbonatto.fintrack.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public List<AccountResponse> getAllForUser(User user) {
        return accountRepository.findByUserId(user.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    public AccountBalancesResponse getBalancesForUser(User user) {
        List<Account> accounts = accountRepository.findByUserId(user.getId());
        Map<Currency, BigDecimal> balancesByCurrency = new EnumMap<>(Currency.class);

        accounts.forEach(account -> balancesByCurrency.merge(
            account.getCurrency(), account.getBalance(), BigDecimal::add));

        return AccountBalancesResponse.builder()
                .accountCount(accounts.size())
                .balancesByCurrency(balancesByCurrency)
                .build();
    }

    public AccountResponse getById(Long id, User user) {
        return toResponse(getOwnedAccount(id, user));
    }

    public AccountResponse create(AccountRequest request, User user) {
        Account account = Account.builder()
                .name(request.getName())
                .currency(request.getCurrency())
                .balance(BigDecimal.ZERO)
                .user(user)
                .build();

        return toResponse(accountRepository.save(account));
    }

    public AccountResponse update(Long id, AccountRequest request, User user) {
        Account account = getOwnedAccount(id, user);
        account.setName(request.getName());
        account.setCurrency(request.getCurrency());

        return toResponse(accountRepository.save(account));
    }

    public void delete(Long id, User user) {
        Account account = getOwnedAccount(id, user);
        accountRepository.delete(account);
    }

    Account getOwnedAccount(Long id, User user) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta", id));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("No tenés acceso a esta cuenta");
        }

        return account;
    }

    private AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .currency(account.getCurrency())
                .balance(account.getBalance())
                .createdAt(account.getCreatedAt())
                .build();
    }
}
