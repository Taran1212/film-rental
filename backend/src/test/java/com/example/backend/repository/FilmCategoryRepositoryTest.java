package com.example.backend.repository;

import com.example.backend.entity.Category;
import com.example.backend.entity.Film;
import com.example.backend.entity.FilmCategory;
import com.example.backend.entity.FilmCategoryId;
import com.example.backend.entity.Language;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FilmCategoryRepositoryTest {

    @Autowired
    private FilmCategoryRepository filmCategoryRepository;

    @Autowired
    private EntityManager entityManager;

    private Pageable pageable;

    @BeforeEach
    void setUp() {

        pageable = PageRequest.of(0, 10);

        Language language = new Language();
        language.setName("T");
        language.setLastUpdate(LocalDateTime.now());

        entityManager.persist(language);

        Category actionCategory = new Category();
        actionCategory.setName("TestActionCategory");
        actionCategory.setLastUpdate(LocalDateTime.now());

        Category comedyCategory = new Category();
        comedyCategory.setName("TestComedyCategory");
        comedyCategory.setLastUpdate(LocalDateTime.now());

        entityManager.persist(actionCategory);
        entityManager.persist(comedyCategory);

        Film film1 = new Film();
        film1.setTitle("Batman Begins");
        film1.setDescription("Action movie");
        film1.setReleaseYear(2005);
        film1.setRentalDuration(5);
        film1.setRentalRate(BigDecimal.valueOf(4.99));
        film1.setLength(120);
        film1.setReplacementCost(BigDecimal.valueOf(19.99));
        film1.setRating("PG-13");
        film1.setSpecialFeatures("Trailers");
        film1.setLastUpdate(LocalDateTime.now());
        film1.setLanguage(language);

        Film film2 = new Film();
        film2.setTitle("The Mask");
        film2.setDescription("Comedy movie");
        film2.setReleaseYear(1994);
        film2.setRentalDuration(5);
        film2.setRentalRate(BigDecimal.valueOf(3.99));
        film2.setLength(110);
        film2.setReplacementCost(BigDecimal.valueOf(17.99));
        film2.setRating("PG");
        film2.setSpecialFeatures("Deleted Scenes");
        film2.setLastUpdate(LocalDateTime.now());
        film2.setLanguage(language);

        entityManager.persist(film1);
        entityManager.persist(film2);

        entityManager.flush();

        FilmCategory filmCategory1 = new FilmCategory();

        FilmCategoryId id1 = new FilmCategoryId();
        id1.setFilmId(film1.getFilmId());
        id1.setCategoryId(actionCategory.getCategoryId());

        filmCategory1.setId(id1);
        filmCategory1.setFilm(film1);
        filmCategory1.setCategory(actionCategory);
        filmCategory1.setLastUpdate(LocalDateTime.now());

        FilmCategory filmCategory2 = new FilmCategory();

        FilmCategoryId id2 = new FilmCategoryId();
        id2.setFilmId(film2.getFilmId());
        id2.setCategoryId(comedyCategory.getCategoryId());

        filmCategory2.setId(id2);
        filmCategory2.setFilm(film2);
        filmCategory2.setCategory(comedyCategory);
        filmCategory2.setLastUpdate(LocalDateTime.now());

        filmCategoryRepository.save(filmCategory1);
        filmCategoryRepository.save(filmCategory2);
    }

    @Test
    @DisplayName("Should find film categories by category name")
    void testFindByCategoryNameIgnoreCase() {

        Page<FilmCategory> result =
                filmCategoryRepository.findByCategory_NameIgnoreCase(
                        "TestActionCategory",
                        pageable
                );

        assertThat(result.getContent()).hasSize(1);

        FilmCategory filmCategory = result.getContent().get(0);

        assertThat(filmCategory.getCategory().getName())
                .isEqualTo("TestActionCategory");

        assertThat(filmCategory.getFilm().getTitle())
                .isEqualTo("Batman Begins");
    }
}