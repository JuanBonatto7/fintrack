package com.juanbonatto.fintrack.config;

import com.juanbonatto.fintrack.model.Category;
import com.juanbonatto.fintrack.model.CategoryType;
import com.juanbonatto.fintrack.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CategoryDataInitializer {

    private final CategoryRepository categoryRepository;

    @Bean
    CommandLineRunner seedDefaultCategories() {
        return args -> defaultCategories().forEach(this::saveIfMissing);
    }

    private void saveIfMissing(Category category) {
        if (!categoryRepository.existsByNameAndType(category.getName(), category.getType())) {
            categoryRepository.save(category);
        }
    }

    private List<Category> defaultCategories() {
        return List.of(
                category("Salary", CategoryType.INCOME, "S", "#7450b5"),
                category("Freelance", CategoryType.INCOME, "F", "#8c68c7"),
                category("Investments", CategoryType.INCOME, "I", "#b08dde"),
                category("Food & dining", CategoryType.EXPENSE, "FD", "#d978a2"),
                category("Housing", CategoryType.EXPENSE, "H", "#8b9fd2"),
                category("Transport", CategoryType.EXPENSE, "T", "#72b88a"),
                category("Health", CategoryType.EXPENSE, "H", "#d9a46f"),
                category("Entertainment", CategoryType.EXPENSE, "E", "#d28ac0"),
                category("Shopping", CategoryType.EXPENSE, "S", "#c88973"),
                category("Other", CategoryType.BOTH, "O", "#9b91a3")
        );
    }

    private Category category(String name, CategoryType type, String icon, String color) {
        return Category.builder()
                .name(name)
                .type(type)
                .icon(icon)
                .color(color)
                .build();
    }
}