package com.juanbonatto.fintrack.controller;

import com.juanbonatto.fintrack.AbstractIntegrationTest;
import com.juanbonatto.fintrack.dto.request.TransactionRequest;
import com.juanbonatto.fintrack.model.CategoryType;
import com.juanbonatto.fintrack.model.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TransactionControllerTest extends AbstractIntegrationTest {

    @Test
    void incomeTransactionIncreasesAccountBalance() throws Exception {
        String token = registerAndGetToken("Juan", "tx1@example.com", "password123");
        Long accountId = createAccount(token, "Cuenta");
        Long categoryId = createCategory(token, "Sueldo", CategoryType.INCOME);

        createTransaction(token, accountId, categoryId, TransactionType.INCOME, "1000");

        mockMvc.perform(get("/api/accounts/" + accountId).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1000.0));
    }

    @Test
    void expenseTransactionDecreasesAccountBalance() throws Exception {
        String token = registerAndGetToken("Juan", "tx2@example.com", "password123");
        Long accountId = createAccount(token, "Cuenta");
        Long incomeCategoryId = createCategory(token, "Sueldo", CategoryType.INCOME);
        Long expenseCategoryId = createCategory(token, "Comida", CategoryType.EXPENSE);

        createTransaction(token, accountId, incomeCategoryId, TransactionType.INCOME, "1000");
        createTransaction(token, accountId, expenseCategoryId, TransactionType.EXPENSE, "300");

        mockMvc.perform(get("/api/accounts/" + accountId).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(700.0));
    }

    @Test
    void deletingTransactionRevertsBalance() throws Exception {
        String token = registerAndGetToken("Juan", "tx3@example.com", "password123");
        Long accountId = createAccount(token, "Cuenta");
        Long categoryId = createCategory(token, "Sueldo", CategoryType.INCOME);

        Long transactionId = createTransaction(token, accountId, categoryId, TransactionType.INCOME, "1000");

        mockMvc.perform(delete("/api/transactions/" + transactionId).header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/accounts/" + accountId).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(0.0));
    }

    @Test
    void updatingTransactionAmountAdjustsBalanceCorrectly() throws Exception {
        String token = registerAndGetToken("Juan", "tx4@example.com", "password123");
        Long accountId = createAccount(token, "Cuenta");
        Long categoryId = createCategory(token, "Sueldo", CategoryType.INCOME);

        Long transactionId = createTransaction(token, accountId, categoryId, TransactionType.INCOME, "1000");

        TransactionRequest updateRequest = new TransactionRequest(
                new BigDecimal("1500"), "Sueldo actualizado", LocalDate.now(), TransactionType.INCOME, accountId, categoryId
        );

        mockMvc.perform(put("/api/transactions/" + transactionId)
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/accounts/" + accountId).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1500.0));
    }

    @Test
    void creatingTransactionOnAnotherUsersAccountIsForbidden() throws Exception {
        String tokenA = registerAndGetToken("A", "txA@example.com", "password123");
        String tokenB = registerAndGetToken("B", "txB@example.com", "password123");

        Long accountId = createAccount(tokenA, "Cuenta de A");
        Long categoryId = createCategory(tokenB, "Categoria de B", CategoryType.INCOME);

        TransactionRequest request = new TransactionRequest(
                new BigDecimal("100"), "intento", LocalDate.now(), TransactionType.INCOME, accountId, categoryId
        );

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    private Long createTransaction(String token, Long accountId, Long categoryId, TransactionType type, String amount) throws Exception {
        TransactionRequest request = new TransactionRequest(
                new BigDecimal(amount), "desc", LocalDate.now(), type, accountId, categoryId
        );

        MvcResult result = mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }
}
