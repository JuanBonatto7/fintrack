package com.juanbonatto.fintrack.controller;

import com.juanbonatto.fintrack.dto.request.AccountRequest;
import com.juanbonatto.fintrack.dto.response.AccountResponse;
import com.juanbonatto.fintrack.model.User;
import com.juanbonatto.fintrack.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAll(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountService.getAllForUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountService.getById(id, user));
    }

    @PostMapping
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody AccountRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.create(request, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponse> update(@PathVariable Long id, @Valid @RequestBody AccountRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountService.update(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        accountService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
