package com.movie_rental_backend.repository;

import com.movie_rental_backend.entity.Category;
import com.movie_rental_backend.entity.FilmCategory;
import com.movie_rental_backend.entity.FilmCategoryId;
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