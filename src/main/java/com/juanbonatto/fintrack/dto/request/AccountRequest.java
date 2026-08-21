package com.juanbonatto.fintrack.dto.request;

import com.juanbonatto.fintrack.model.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {

    @NotBlank
    private String name;

    @NotNull
    private Currency currency;
}
