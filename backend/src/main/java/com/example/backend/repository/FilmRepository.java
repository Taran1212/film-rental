package com.example.backend.repository;

import com.example.backend.entity.Film;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmRepository extends JpaRepository<Film, Integer> {

    Page<Film> findByTitleContainingIgnoreCase(String title, Pageable pageable);


    Page<Film> findDistinctByFilmActors_Actor_FirstNameContainingIgnoreCaseOrFilmActors_Actor_LastNameContainingIgnoreCase(
            String firstName,
            String lastName,
            Pageable pageable
    );


    Page<Film> findDistinctByFilmActors_Actor_FirstNameContainingIgnoreCaseAndFilmActors_Actor_LastNameContainingIgnoreCase(
            String firstName,
            String lastName,
            Pageable pageable
    );


    Page<Film> findDistinctByFilmCategories_Category_NameIgnoreCase(
            String categoryName,
            Pageable pageable
    );

    Page<Film> findDistinctByInventories_Store_StoreId(Integer storeId, Pageable pageable);

    Page<Film> findDistinctByInventories_Store_StoreIdAndTitleContainingIgnoreCase(
            Integer storeId, String title, Pageable pageable);
}