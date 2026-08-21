package com.juanbonatto.fintrack.dto.request;

import com.juanbonatto.fintrack.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

    @NotNull
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal amount;

    private String description;

    @NotNull
    private LocalDate date;

    @NotNull
    private TransactionType type;

    @NotNull
    private Long accountId;

    @NotNull
    private Long categoryId;
}
