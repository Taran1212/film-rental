package com.example.backend.repository;

import com.example.backend.entity.FilmCategory;
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
class FilmCategoryRepositoryTest {

    @Autowired
    private FilmCategoryRepository filmCategoryRepository;

    @Test
    @DisplayName("Should find films by category name")
    void shouldFindFilmsByCategoryName() {

        Page<FilmCategory> result =
                filmCategoryRepository.findByCategory_NameIgnoreCase(
                        "Action",
                        PageRequest.of(0, 10)
                );

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
    }
}