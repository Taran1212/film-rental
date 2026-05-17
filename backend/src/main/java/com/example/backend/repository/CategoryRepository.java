package com.example.backend.repository;

import com.example.backend.dto.projection.CategoryProjection;
import com.example.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<CategoryProjection> findAllByOrderByNameAsc();
}
