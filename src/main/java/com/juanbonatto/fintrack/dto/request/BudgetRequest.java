package com.juanbonatto.fintrack.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRequest {

    @NotNull
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal amount;

    @NotNull
    @Min(1)
    @Max(12)
    private Integer month;

    @NotNull
    private Integer year;

    @NotNull
    private Long accountId;

    @NotNull
    private Long categoryId;
}
