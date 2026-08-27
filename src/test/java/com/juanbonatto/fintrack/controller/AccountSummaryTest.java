package com.juanbonatto.fintrack.controller;

import com.juanbonatto.fintrack.AbstractIntegrationTest;
import com.juanbonatto.fintrack.dto.request.AccountRequest;
import com.juanbonatto.fintrack.dto.request.BudgetRequest;
import com.juanbonatto.fintrack.dto.request.TransactionRequest;
import com.juanbonatto.fintrack.model.CategoryType;
import com.juanbonatto.fintrack.model.Currency;
import com.juanbonatto.fintrack.model.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountSummaryTest extends AbstractIntegrationTest {

    @Test
    void balancesSummaryKeepsCurrenciesSeparate() throws Exception {
        String token = registerAndGetToken("Juan", "summaryCurrencies@example.com", "password123");
        Long arsAccountId = createAccount(token, "Cuenta ARS");
        AccountRequest usdRequest = new AccountRequest("Cuenta USD", Currency.USD);

        mockMvc.perform(post("/api/accounts")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(usdRequest)))
                .andExpect(status().isCreated());

        Long categoryId = createCategory(token, "Sueldo", CategoryType.INCOME);
        createTransaction(token, arsAccountId, categoryId, TransactionType.INCOME, "1000", LocalDate.now());

        mockMvc.perform(get("/api/accounts/summary")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountCount").value(2))
                .andExpect(jsonPath("$.balancesByCurrency.ARS").value(1000.0))
                .andExpect(jsonPath("$.balancesByCurrency.USD").value(0.0))
                .andExpect(jsonPath("$.totalBalance").doesNotExist());
    }

    @Test
    void summaryAggregatesIncomeExpenseAndBudgets() throws Exception {
        String token = registerAndGetToken("Juan", "summary1@example.com", "password123");
        Long accountId = createAccount(token, "Cuenta");
        Long incomeCategoryId = createCategory(token, "Sueldo", CategoryType.INCOME);
        Long expenseCategoryId = createCategory(token, "Comida", CategoryType.EXPENSE);

        LocalDate thisMonth = LocalDate.now().withDayOfMonth(1);

        createTransaction(token, accountId, incomeCategoryId, TransactionType.INCOME, "2000", thisMonth);
        createTransaction(token, accountId, expenseCategoryId, TransactionType.EXPENSE, "300", thisMonth);

        BudgetRequest budget = new BudgetRequest(
                new BigDecimal("500"), thisMonth.getMonthValue(), thisMonth.getYear(), accountId, expenseCategoryId
        );
        mockMvc.perform(post("/api/budgets")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(budget)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/accounts/" + accountId + "/summary")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").value(2000.0))
                .andExpect(jsonPath("$.totalExpense").value(300.0))
                .andExpect(jsonPath("$.net").value(1700.0))
                .andExpect(jsonPath("$.balance").value(1700.0))
                .andExpect(jsonPath("$.categorySummaries.length()").value(1))
                .andExpect(jsonPath("$.categorySummaries[0].budgeted").value(500.0))
                .andExpect(jsonPath("$.categorySummaries[0].spent").value(300.0))
                .andExpect(jsonPath("$.categorySummaries[0].remaining").value(200.0));
    }

    @Test
    void summaryForAnotherUsersAccountIsForbidden() throws Exception {
        String tokenA = registerAndGetToken("A", "summaryA@example.com", "password123");
        String tokenB = registerAndGetToken("B", "summaryB@example.com", "password123");

        Long accountId = createAccount(tokenA, "Cuenta de A");

        mockMvc.perform(get("/api/accounts/" + accountId + "/summary")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isForbidden());
    }

    private void createTransaction(String token, Long accountId, Long categoryId, TransactionType type, String amount, LocalDate date) throws Exception {
        TransactionRequest request = new TransactionRequest(new BigDecimal(amount), "desc", date, type, accountId, categoryId);

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
