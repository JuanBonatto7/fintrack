package com.juanbonatto.fintrack.controller;

import com.juanbonatto.fintrack.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthMeTest extends AbstractIntegrationTest {

    @Test
    void meReturnsCurrentUserWhenAuthenticated() throws Exception {
        String token = registerAndGetToken("Juan", "me1@example.com", "password123");

        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("me1@example.com"))
                .andExpect(jsonPath("$.name").value("Juan"));
    }

    @Test
    void meWithoutTokenIsRejected() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isForbidden());
    }
}
