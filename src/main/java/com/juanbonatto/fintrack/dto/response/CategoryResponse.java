package com.juanbonatto.fintrack.dto.response;

import com.juanbonatto.fintrack.model.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {

    private Long id;
    private String name;
    private CategoryType type;
    private String icon;
    private String color;
}
