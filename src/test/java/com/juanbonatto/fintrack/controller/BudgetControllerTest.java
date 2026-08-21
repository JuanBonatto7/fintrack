package com.juanbonatto.fintrack.controller;

import com.juanbonatto.fintrack.AbstractIntegrationTest;
import com.juanbonatto.fintrack.dto.request.BudgetRequest;
import com.juanbonatto.fintrack.model.CategoryType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BudgetControllerTest extends AbstractIntegrationTest {

    @Test
    void createBudgetSucceeds() throws Exception {
        String token = registerAndGetToken("Juan", "budget1@example.com", "password123");
        Long accountId = createAccount(token, "Cuenta");
        Long categoryId = createCategory(token, "Comida", CategoryType.EXPENSE);

        BudgetRequest request = new BudgetRequest(new BigDecimal("500"), 8, 2026, accountId, categoryId);

        mockMvc.perform(post("/api/budgets")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void creatingDuplicateBudgetReturnsConflict() throws Exception {
        String token = registerAndGetToken("Juan", "budget2@example.com", "password123");
        Long accountId = createAccount(token, "Cuenta");
        Long categoryId = createCategory(token, "Comida", CategoryType.EXPENSE);

        BudgetRequest request = new BudgetRequest(new BigDecimal("500"), 8, 2026, accountId, categoryId);

        mockMvc.perform(post("/api/budgets")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        BudgetRequest duplicate = new BudgetRequest(new BigDecimal("999"), 8, 2026, accountId, categoryId);

        mockMvc.perform(post("/api/budgets")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict());
    }

    @Test
    void sameCategoryDifferentMonthIsAllowed() throws Exception {
        String token = registerAndGetToken("Juan", "budget3@example.com", "password123");
        Long accountId = createAccount(token, "Cuenta");
        Long categoryId = createCategory(token, "Comida", CategoryType.EXPENSE);

        BudgetRequest august = new BudgetRequest(new BigDecimal("500"), 8, 2026, accountId, categoryId);
        BudgetRequest september = new BudgetRequest(new BigDecimal("500"), 9, 2026, accountId, categoryId);

        mockMvc.perform(post("/api/budgets")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(august)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/budgets")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(september)))
                .andExpect(status().isCreated());
    }
}
