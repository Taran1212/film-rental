package com.example.backend.repository;

import com.example.backend.entity.FilmCategory;
import com.example.backend.entity.FilmCategoryId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmCategoryRepository
        extends JpaRepository<FilmCategory, FilmCategoryId> {

    Page<FilmCategory> findByCategory_NameIgnoreCase(
            String categoryName,
            Pageable pageable
    );
}