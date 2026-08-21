package com.juanbonatto.fintrack.controller;

import com.juanbonatto.fintrack.dto.request.TransactionRequest;
import com.juanbonatto.fintrack.dto.response.TransactionResponse;
import com.juanbonatto.fintrack.model.User;
import com.juanbonatto.fintrack.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllForAccount(
            @RequestParam Long accountId, @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(transactionService.getAllForAccount(accountId, user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(transactionService.getById(id, user));
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.create(request, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(@PathVariable Long id, @Valid @RequestBody TransactionRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(transactionService.update(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        transactionService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
