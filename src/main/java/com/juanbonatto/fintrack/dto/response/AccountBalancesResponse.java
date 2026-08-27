package com.juanbonatto.fintrack.dto.response;

import com.juanbonatto.fintrack.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountBalancesResponse {

    private int accountCount;
    private Map<Currency, BigDecimal> balancesByCurrency;
}
