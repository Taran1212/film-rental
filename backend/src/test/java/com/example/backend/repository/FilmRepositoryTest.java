package com.example.backend.repository;

import com.example.backend.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
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
@Transactional
class FilmRepositoryTest {

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private EntityManager entityManager;

    private Pageable pageable;

    private Film inception;
    private Film titanic;

    @BeforeEach
    void setUp() {

        pageable = PageRequest.of(0, 10);

        Language language = new Language();
        language.setName("T");
        language.setLastUpdate(LocalDateTime.now());

        entityManager.persist(language);

        Actor leonardo = new Actor();
        leonardo.setFirstName("TestLeonardo");
        leonardo.setLastName("TestDiCaprio");
        leonardo.setLastUpdate(LocalDateTime.now());

        Actor tom = new Actor();
        tom.setFirstName("TestTom");
        tom.setLastName("TestHanks");
        tom.setLastUpdate(LocalDateTime.now());

        entityManager.persist(leonardo);
        entityManager.persist(tom);

        Category dramaCategory = new Category();
        dramaCategory.setName("TestDrama");
        dramaCategory.setLastUpdate(LocalDateTime.now());

        Category actionCategory = new Category();
        actionCategory.setName("TestAction");
        actionCategory.setLastUpdate(LocalDateTime.now());

        entityManager.persist(dramaCategory);
        entityManager.persist(actionCategory);

        inception = new Film();
        inception.setTitle("Test Inception");
        inception.setDescription("Sci-fi movie");
        inception.setReleaseYear(2010);
        inception.setRentalDuration(5);
        inception.setRentalRate(BigDecimal.valueOf(4.99));
        inception.setLength(140);
        inception.setReplacementCost(BigDecimal.valueOf(19.99));
        inception.setRating("PG-13");
        inception.setSpecialFeatures("Trailers");
        inception.setLastUpdate(LocalDateTime.now());
        inception.setLanguage(language);

        titanic = new Film();
        titanic.setTitle("Test Titanic");
        titanic.setDescription("Romantic movie");
        titanic.setReleaseYear(1997);
        titanic.setRentalDuration(5);
        titanic.setRentalRate(BigDecimal.valueOf(3.99));
        titanic.setLength(180);
        titanic.setReplacementCost(BigDecimal.valueOf(21.99));
        titanic.setRating("PG-13");
        titanic.setSpecialFeatures("Deleted Scenes");
        titanic.setLastUpdate(LocalDateTime.now());
        titanic.setLanguage(language);

        entityManager.persist(inception);
        entityManager.persist(titanic);

        entityManager.flush();

        FilmActor filmActor1 = new FilmActor();

        FilmActorId filmActorId1 = new FilmActorId();
        filmActorId1.setActorId(leonardo.getActorId());
        filmActorId1.setFilmId(inception.getFilmId());

        filmActor1.setId(filmActorId1);
        filmActor1.setActor(leonardo);
        filmActor1.setFilm(inception);
        filmActor1.setLastUpdate(LocalDateTime.now());

        FilmActor filmActor2 = new FilmActor();

        FilmActorId filmActorId2 = new FilmActorId();
        filmActorId2.setActorId(tom.getActorId());
        filmActorId2.setFilmId(titanic.getFilmId());

        filmActor2.setId(filmActorId2);
        filmActor2.setActor(tom);
        filmActor2.setFilm(titanic);
        filmActor2.setLastUpdate(LocalDateTime.now());

        entityManager.persist(filmActor1);
        entityManager.persist(filmActor2);

        FilmCategory filmCategory1 = new FilmCategory();

        FilmCategoryId filmCategoryId1 = new FilmCategoryId();
        filmCategoryId1.setFilmId(inception.getFilmId());
        filmCategoryId1.setCategoryId(actionCategory.getCategoryId());

        filmCategory1.setId(filmCategoryId1);
        filmCategory1.setFilm(inception);
        filmCategory1.setCategory(actionCategory);
        filmCategory1.setLastUpdate(LocalDateTime.now());

        FilmCategory filmCategory2 = new FilmCategory();

        FilmCategoryId filmCategoryId2 = new FilmCategoryId();
        filmCategoryId2.setFilmId(titanic.getFilmId());
        filmCategoryId2.setCategoryId(dramaCategory.getCategoryId());

        filmCategory2.setId(filmCategoryId2);
        filmCategory2.setFilm(titanic);
        filmCategory2.setCategory(dramaCategory);
        filmCategory2.setLastUpdate(LocalDateTime.now());

        entityManager.persist(filmCategory1);
        entityManager.persist(filmCategory2);

        Store store1 = entityManager.getReference(Store.class, 1);
        Store store2 = entityManager.getReference(Store.class, 2);

        Inventory inventory1 = new Inventory();
        inventory1.setFilm(inception);
        inventory1.setStore(store1);
        inventory1.setLastUpdate(LocalDateTime.now());

        Inventory inventory2 = new Inventory();
        inventory2.setFilm(titanic);
        inventory2.setStore(store2);
        inventory2.setLastUpdate(LocalDateTime.now());

        entityManager.persist(inventory1);
        entityManager.persist(inventory2);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should find films by title")
    void testFindByTitleContainingIgnoreCase() {

        Page<Film> result =
                filmRepository.findByTitleContainingIgnoreCase(
                        "Inception",
                        pageable
                );

        assertThat(result.getContent()).hasSize(1);

        assertThat(result.getContent().get(0).getTitle())
                .isEqualTo("Test Inception");
    }

    @Test
    @DisplayName("Should find films by actor first name or last name")
    void testFindDistinctByFilmActorsActorFirstNameContainingIgnoreCaseOrFilmActorsActorLastNameContainingIgnoreCase() {

        Page<Film> result =
                filmRepository
                        .findDistinctByFilmActors_Actor_FirstNameContainingIgnoreCaseOrFilmActors_Actor_LastNameContainingIgnoreCase(
                                "TestLeonardo",
                                "TestLeonardo",
                                pageable
                        );

        assertThat(result.getContent()).hasSize(1);

        assertThat(result.getContent().get(0).getTitle())
                .isEqualTo("Test Inception");
    }

    @Test
    @DisplayName("Should find films by actor full name")
    void testFindDistinctByFilmActorsActorFirstNameContainingIgnoreCaseAndFilmActorsActorLastNameContainingIgnoreCase() {

        Page<Film> result =
                filmRepository
                        .findDistinctByFilmActors_Actor_FirstNameContainingIgnoreCaseAndFilmActors_Actor_LastNameContainingIgnoreCase(
                                "TestLeonardo",
                                "TestDiCaprio",
                                pageable
                        );

        assertThat(result.getContent()).hasSize(1);

        assertThat(result.getContent().get(0).getTitle())
                .isEqualTo("Test Inception");
    }

    @Test
    @DisplayName("Should find films by category name")
    void testFindDistinctByFilmCategoriesCategoryNameIgnoreCase() {

        Page<Film> result =
                filmRepository
                        .findDistinctByFilmCategories_Category_NameIgnoreCase(
                                "TestAction",
                                pageable
                        );

        assertThat(result.getContent()).hasSize(1);

        assertThat(result.getContent().get(0).getTitle())
                .isEqualTo("Test Inception");
    }

    @Test
    @DisplayName("Should find films by store id")
    void testFindDistinctByInventoriesStoreId() {

        Page<Film> result =
                filmRepository.findDistinctByInventories_Store_StoreId(
                        1,
                        pageable
                );

        assertThat(result.getContent()).isNotEmpty();

        assertThat(result.getContent())
                .extracting(Film::getFilmId)
                .doesNotHaveDuplicates();

        assertThat(result.getContent())
                .extracting(Film::getInventories)
                .allMatch(invs ->
                        invs.stream()
                                .anyMatch(i -> i.getStore().getStoreId() == 1)
                );
    }

    @Test
    @DisplayName("Should find films by store id and title")
    void testFindDistinctByInventoriesStoreIdAndTitleContainingIgnoreCase() {

        Page<Film> result =
                filmRepository
                        .findDistinctByInventories_Store_StoreIdAndTitleContainingIgnoreCase(
                                1,
                                "Inception",
                                pageable
                        );

        assertThat(result.getContent()).hasSize(1);

        assertThat(result.getContent().get(0).getTitle())
                .isEqualTo("Test Inception");
    }
}