package com.example.backend.repository;

import com.example.backend.entity.Film;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FilmRepositoryTest {

    @Autowired
    private FilmRepository filmRepository;

    @Test
    @DisplayName("Should find films by title")
    void shouldFindFilmsByTitle() {

        Page<Film> result =
                filmRepository.findByTitleContainingIgnoreCase(
                        "ACADEMY",
                        PageRequest.of(0, 10)
                );

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("Should find films by actor first name or last name")
    void shouldFindFilmsByActorFirstNameOrLastName() {

        Page<Film> result =
                filmRepository
                        .findDistinctByFilmActors_Actor_FirstNameContainingIgnoreCaseOrFilmActors_Actor_LastNameContainingIgnoreCase(
                                "PENELOPE",
                                "GUINESS",
                                PageRequest.of(0, 10)
                        );

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("Should find films by actor full name")
    void shouldFindFilmsByActorFullName() {

        Page<Film> result =
                filmRepository
                        .findDistinctByFilmActors_Actor_FirstNameContainingIgnoreCaseAndFilmActors_Actor_LastNameContainingIgnoreCase(
                                "PENELOPE",
                                "GUINESS",
                                PageRequest.of(0, 10)
                        );

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("Should find films by category name")
    void shouldFindFilmsByCategoryName() {

        Page<Film> result =
                filmRepository.findDistinctByFilmCategories_Category_NameIgnoreCase(
                        "Action",
                        PageRequest.of(0, 10)
                );

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
    }
}