package com.example.backend.repository;

import com.example.backend.dto.projection.ActorProjection;
import com.example.backend.entity.Actor;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ActorRepositoryTest {

    @Autowired
    private ActorRepository actorRepository;

    private Pageable pageable;

    @BeforeEach
    void setUp() {

        pageable = PageRequest.of(0, 10);

        Actor actor1 = new Actor();
        actor1.setFirstName("TestTom");
        actor1.setLastName("TestHanks");
        actor1.setLastUpdate(LocalDateTime.now());

        Actor actor2 = new Actor();
        actor2.setFirstName("TestLeonardo");
        actor2.setLastName("TestDiCaprio");
        actor2.setLastUpdate(LocalDateTime.now());

        actorRepository.save(actor1);
        actorRepository.save(actor2);
    }

    @Test
    @DisplayName("Should find all actors")
    void testFindAll() {

        Page<Actor> result = actorRepository.findAll(pageable);

        assertThat(result.getContent().size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should find actors by first name or last name with projection")
    void testFindByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseWithPageable() {

        Page<ActorProjection> result =
                actorRepository
                        .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                                "TestTom",
                                "TestTom",
                                pageable
                        );

        assertThat(result.getContent()).hasSize(1);

        ActorProjection actor = result.getContent().get(0);

        assertThat(actor.getActorName()).isEqualTo("TestTom TestHanks");
    }

    @Test
    @DisplayName("Should find actors by first name and last name with pageable")
    void testFindByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCaseWithPageable() {

        Page<ActorProjection> result =
                actorRepository
                        .findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(
                                "TestTom",
                                "TestHanks",
                                pageable
                        );

        assertThat(result.getContent()).hasSize(1);

        ActorProjection actor = result.getContent().get(0);

        assertThat(actor.getActorName()).isEqualTo("TestTom TestHanks");
    }
}