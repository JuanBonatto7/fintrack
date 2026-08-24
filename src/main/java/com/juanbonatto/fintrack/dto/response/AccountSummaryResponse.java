package com.juanbonatto.fintrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountSummaryResponse {

    private Long accountId;
    private Integer month;
    private Integer year;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal net;
    private BigDecimal balance;
    private List<CategoryBudgetSummary> categorySummaries;
}
