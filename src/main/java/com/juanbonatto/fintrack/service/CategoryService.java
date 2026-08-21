package com.juanbonatto.fintrack.service;

import com.juanbonatto.fintrack.dto.request.CategoryRequest;
import com.juanbonatto.fintrack.dto.response.CategoryResponse;
import com.juanbonatto.fintrack.exception.ResourceNotFoundException;
import com.juanbonatto.fintrack.model.Category;
import com.juanbonatto.fintrack.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    public CategoryResponse create(CategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .type(request.getType())
                .icon(request.getIcon())
                .color(request.getColor())
                .build();

        return toResponse(categoryRepository.save(category));
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findEntity(id);
        category.setName(request.getName());
        category.setType(request.getType());
        category.setIcon(request.getIcon());
        category.setColor(request.getColor());

        return toResponse(categoryRepository.save(category));
    }

    public void delete(Long id) {
        Category category = findEntity(id);
        categoryRepository.delete(category);
    }

    Category findEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", id));
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .icon(category.getIcon())
                .color(category.getColor())
                .build();
    }
}
