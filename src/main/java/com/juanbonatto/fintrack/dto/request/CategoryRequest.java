package com.juanbonatto.fintrack.dto.request;

import com.juanbonatto.fintrack.model.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank
    private String name;

    @NotNull
    private CategoryType type;

    private String icon;

    private String color;
}
