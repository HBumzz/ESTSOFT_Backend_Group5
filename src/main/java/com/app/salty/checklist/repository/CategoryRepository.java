package com.app.salty.checklist.repository;

import com.app.salty.checklist.entity.Category;
import com.app.salty.checklist.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByCategoryType(CategoryType categoryType);
}
