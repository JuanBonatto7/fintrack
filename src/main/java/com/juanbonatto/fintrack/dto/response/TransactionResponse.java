package com.juanbonatto.fintrack.dto.response;

import com.juanbonatto.fintrack.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private Long id;
    private BigDecimal amount;
    private String description;
    private LocalDate date;
    private TransactionType type;
    private Long accountId;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
}
