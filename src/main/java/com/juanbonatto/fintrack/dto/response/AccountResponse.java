package com.juanbonatto.fintrack.dto.response;

import com.juanbonatto.fintrack.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {

    private Long id;
    private String name;
    private Currency currency;
    private BigDecimal balance;
    private LocalDateTime createdAt;
}
