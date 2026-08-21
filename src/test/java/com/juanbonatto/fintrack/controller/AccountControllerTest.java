package com.juanbonatto.fintrack.controller;

import com.juanbonatto.fintrack.AbstractIntegrationTest;
import com.juanbonatto.fintrack.dto.request.AccountRequest;
import com.juanbonatto.fintrack.model.Currency;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AccountControllerTest extends AbstractIntegrationTest {

    @Test
    void createAccountStartsWithZeroBalance() throws Exception {
        String token = registerAndGetToken("Juan", "accounts1@example.com", "password123");
        AccountRequest request = new AccountRequest("Cuenta principal", Currency.ARS);

        mockMvc.perform(post("/api/accounts")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Cuenta principal"))
                .andExpect(jsonPath("$.balance").value(0));
    }

    @Test
    void listAccountsOnlyReturnsOwnAccounts() throws Exception {
        String tokenA = registerAndGetToken("A", "ownerA@example.com", "password123");
        String tokenB = registerAndGetToken("B", "ownerB@example.com", "password123");

        createAccount(tokenA, "Cuenta de A");
        createAccount(tokenB, "Cuenta de B");

        mockMvc.perform(get("/api/accounts").header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Cuenta de A"));
    }

    @Test
    void accessingAnotherUsersAccountReturnsForbidden() throws Exception {
        String tokenA = registerAndGetToken("A", "forbiddenA@example.com", "password123");
        String tokenB = registerAndGetToken("B", "forbiddenB@example.com", "password123");

        Long accountId = createAccount(tokenA, "Cuenta privada");

        mockMvc.perform(get("/api/accounts/" + accountId).header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isForbidden());
    }

    @Test
    void requestWithoutTokenIsRejected() throws Exception {
        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletingAccountRemovesItFromList() throws Exception {
        String token = registerAndGetToken("Juan", "delete@example.com", "password123");
        Long accountId = createAccount(token, "Cuenta a borrar");

        mockMvc.perform(delete("/api/accounts/" + accountId).header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/accounts").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
