package com.example.backend.repository;

import com.example.backend.entity.Actor;
import com.example.backend.entity.FilmActor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FilmActorRepositoryTest {

    @Autowired
    private FilmActorRepository filmActorRepository;

//    @Autowired
//    private ActorRepository actorRepository;

//    @Test
//    @DisplayName("Should count films by actor")
//    void shouldCountFilmsByActor() {
//
//        Actor actor = actorRepository.findById(1L).orElse(null);
//
//        Long count = filmActorRepository.countByActor(actor);
//
//        assertThat(count).isNotNull();
//        assertThat(count).isGreaterThanOrEqualTo(0);
//    }

    @Test
    @DisplayName("Should search actor by first name or last name")
    void shouldSearchActorByFirstNameOrLastName() {

        Page<FilmActor> result =
                filmActorRepository
                        .findByActor_FirstNameContainingIgnoreCaseOrActor_LastNameContainingIgnoreCase(
                                "PENELOPE",
                                "GUINESS",
                                PageRequest.of(0, 10)
                        );

        assertThat(result).isNotNull();
    }
}
