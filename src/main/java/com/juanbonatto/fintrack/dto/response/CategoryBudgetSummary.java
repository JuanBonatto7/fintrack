package com.juanbonatto.fintrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryBudgetSummary {

    private Long categoryId;
    private String categoryName;
    private BigDecimal budgeted;
    private BigDecimal spent;
    private BigDecimal remaining;
}
