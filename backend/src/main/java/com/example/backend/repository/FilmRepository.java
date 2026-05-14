package com.example.backend.repository;

import com.example.backend.entity.Film;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmRepository extends JpaRepository<Film, Integer> {

    Page<Film> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // Search films by actor first name OR last name (single keyword)
    Page<Film> findDistinctByFilmActors_Actor_FirstNameContainingIgnoreCaseOrFilmActors_Actor_LastNameContainingIgnoreCase(
            String firstName,
            String lastName,
            Pageable pageable
    );

    // Search films by actor first name AND last name (full name search)
    Page<Film> findDistinctByFilmActors_Actor_FirstNameContainingIgnoreCaseAndFilmActors_Actor_LastNameContainingIgnoreCase(
            String firstName,
            String lastName,
            Pageable pageable
    );

    // Search films by exact category name (case-insensitive)
    Page<Film> findDistinctByFilmCategories_Category_NameIgnoreCase(
            String categoryName,
            Pageable pageable
    );
}