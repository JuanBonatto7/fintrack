package com.juanbonatto.fintrack.controller;

import com.juanbonatto.fintrack.AbstractIntegrationTest;
import com.juanbonatto.fintrack.dto.request.LoginRequest;
import com.juanbonatto.fintrack.dto.request.RegisterRequest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends AbstractIntegrationTest {

    @Test
    void registerCreatesUserAndReturnsToken() throws Exception {
        RegisterRequest request = new RegisterRequest("Juan", "juan@example.com", "password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("juan@example.com"));
    }

    @Test
    void registerWithDuplicateEmailReturnsConflict() throws Exception {
        registerAndGetToken("Juan", "duplicado@example.com", "password123");

        RegisterRequest request = new RegisterRequest("Otro", "duplicado@example.com", "password456");

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void registerWithInvalidEmailReturnsBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest("Juan", "no-es-un-email", "password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginWithCorrectCredentialsReturnsToken() throws Exception {
        registerAndGetToken("Juan", "login@example.com", "password123");

        LoginRequest request = new LoginRequest("login@example.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void loginWithWrongPasswordReturnsUnauthorized() throws Exception {
        registerAndGetToken("Juan", "wrongpass@example.com", "password123");

        LoginRequest request = new LoginRequest("wrongpass@example.com", "incorrecta");

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithUnknownEmailReturnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest("noexiste@example.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
