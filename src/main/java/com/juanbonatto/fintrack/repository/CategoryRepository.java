package com.juanbonatto.fintrack.repository;

import com.juanbonatto.fintrack.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
