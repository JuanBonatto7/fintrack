package com.juanbonatto.fintrack.controller;

import com.juanbonatto.fintrack.dto.request.BudgetRequest;
import com.juanbonatto.fintrack.dto.response.BudgetResponse;
import com.juanbonatto.fintrack.model.User;
import com.juanbonatto.fintrack.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getAllForAccount(
            @RequestParam Long accountId, @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(budgetService.getAllForAccount(accountId, user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponse> getById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(budgetService.getById(id, user));
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> create(@Valid @RequestBody BudgetRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.create(request, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> update(@PathVariable Long id, @Valid @RequestBody BudgetRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(budgetService.update(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        budgetService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
