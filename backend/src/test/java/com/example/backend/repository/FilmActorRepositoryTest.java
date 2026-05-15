package com.example.backend.repository;

import com.example.backend.entity.*;
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
class FilmActorRepositoryTest {

    @Autowired
    private FilmActorRepository filmActorRepository;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private LanguageRepository languageRepository;

    private Actor tomHanks;
    private Actor leonardo;

    private Pageable pageable;

    @BeforeEach
    void setUp() {

        pageable = PageRequest.of(0, 10);

        Language english = new Language();
        english.setName("English_Test");
        english.setLastUpdate(LocalDateTime.now());

        english = languageRepository.save(english);

        tomHanks = new Actor();
        tomHanks.setFirstName("TestTom123");
        tomHanks.setLastName("TestHanks123");
        tomHanks.setLastUpdate(LocalDateTime.now());

        leonardo = new Actor();
        leonardo.setFirstName("TestLeo123");
        leonardo.setLastName("TestDiCaprio123");
        leonardo.setLastUpdate(LocalDateTime.now());

        tomHanks = actorRepository.save(tomHanks);
        leonardo = actorRepository.save(leonardo);

        Film forestGump = new Film();
        forestGump.setTitle("Forest Gump Test");
        forestGump.setDescription("Drama movie");
        forestGump.setReleaseYear(1994);
        forestGump.setRentalDuration(5);
        forestGump.setRentalRate(BigDecimal.valueOf(4.99));
        forestGump.setLength(120);
        forestGump.setReplacementCost(BigDecimal.valueOf(19.99));
        forestGump.setRating("PG-13");
        forestGump.setSpecialFeatures("Behind the Scenes");
        forestGump.setLastUpdate(LocalDateTime.now());
        forestGump.setLanguage(english);

        Film inception = new Film();
        inception.setTitle("Inception Test");
        inception.setDescription("Sci-fi movie");
        inception.setReleaseYear(2010);
        inception.setRentalDuration(5);
        inception.setRentalRate(BigDecimal.valueOf(5.99));
        inception.setLength(148);
        inception.setReplacementCost(BigDecimal.valueOf(24.99));
        inception.setRating("PG-13");
        inception.setSpecialFeatures("Trailers");
        inception.setLastUpdate(LocalDateTime.now());
        inception.setLanguage(english);

        forestGump = filmRepository.save(forestGump);
        inception = filmRepository.save(inception);

        FilmActor filmActor1 = new FilmActor();

        FilmActorId id1 = new FilmActorId();
        id1.setActorId(tomHanks.getActorId());
        id1.setFilmId(forestGump.getFilmId());

        filmActor1.setId(id1);
        filmActor1.setActor(tomHanks);
        filmActor1.setFilm(forestGump);
        filmActor1.setLastUpdate(LocalDateTime.now());

        FilmActor filmActor2 = new FilmActor();

        FilmActorId id2 = new FilmActorId();
        id2.setActorId(leonardo.getActorId());
        id2.setFilmId(inception.getFilmId());

        filmActor2.setId(id2);
        filmActor2.setActor(leonardo);
        filmActor2.setFilm(inception);
        filmActor2.setLastUpdate(LocalDateTime.now());

        filmActorRepository.save(filmActor1);
        filmActorRepository.save(filmActor2);
    }

    @Test
    @DisplayName("Should count film actors by actor")
    void testCountByActor() {

        Long count = filmActorRepository.countByActor(tomHanks);

        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("Should find film actors by actor first name or last name")
    void testFindByActorFirstNameContainingIgnoreCaseOrActorLastNameContainingIgnoreCase() {

        Page<FilmActor> result =
                filmActorRepository
                        .findByActor_FirstNameContainingIgnoreCaseOrActor_LastNameContainingIgnoreCase(
                                "TestTom123",
                                "TestHanks123",
                                pageable
                        );

        assertThat(result.getContent()).hasSize(1);

        FilmActor filmActor = result.getContent().get(0);

        assertThat(filmActor.getActor().getFirstName())
                .isEqualTo("TestTom123");

        assertThat(filmActor.getActor().getLastName())
                .isEqualTo("TestHanks123");
    }
}