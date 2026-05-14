package com.example.backend.repository;

import com.example.backend.entity.Actor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ActorRepositoryTest {
    @Autowired
    private ActorRepository actorRepo;

    @Test
    void testFindAllActors() {
        Page<Actor> actors = actorRepo.findAll(PageRequest.of(0, 5));

        assertNotNull(actors);
        assertFalse(actors.isEmpty());

        System.out.println("Total Actors = " + actors.getTotalElements());

        actors.forEach(actor -> {
            System.out.println("Actor ID: " + actor.getActorId());
            System.out.println("First Name: " + actor.getFirstName());
            System.out.println("Last Name: " + actor.getLastName());
            System.out.println("------------------------");
        });
    }

    @Test
    void testFindAllActorsPagination() {
        Page<Actor> actors = actorRepo.findAll(PageRequest.of(0, 10));

        assertNotNull(actors);
        assertEquals(10, actors.getContent().size());
        assertTrue(actors.getTotalElements() >= 10);
    }

    @Test
    void testSearchByFirstNameOrLastName() {
        Page<Actor> actors =
                actorRepo.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                        "PENELOPE",
                        "WAHLBERG",
                        PageRequest.of(0, 10)
                );

        assertNotNull(actors);
        assertFalse(actors.isEmpty());

        actors.forEach(actor -> {
            System.out.println(actor.getFirstName() + " " + actor.getLastName());
        });

        assertTrue(
                actors.getContent().stream()
                        .anyMatch(actor ->
                                actor.getFirstName().equalsIgnoreCase("PENELOPE")
                                        || actor.getLastName().equalsIgnoreCase("WAHLBERG")
                        )
        );
    }

    @Test
    void testSearchByFirstNameOrLastNameIgnoreCase() {
        Page<Actor> actors =
                actorRepo.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                        "penelope",
                        "wahlberg",
                        PageRequest.of(0, 10)
                );

        assertNotNull(actors);
        assertFalse(actors.isEmpty());
    }

    @Test
    void testSearchByFullNameStrict() {
        Page<Actor> actors =
                actorRepo.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(
                        "PENELOPE",
                        "GUINESS",
                        PageRequest.of(0, 10)
                );

        assertNotNull(actors);
        assertFalse(actors.isEmpty());

        assertTrue(
                actors.getContent().stream()
                        .anyMatch(actor ->
                                actor.getFirstName().equalsIgnoreCase("PENELOPE")
                                        && actor.getLastName().equalsIgnoreCase("GUINESS")
                        )
        );
    }

    @Test
    void testSearchByFullNameStrictIgnoreCase() {
        Page<Actor> actors =
                actorRepo.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(
                        "penelope",
                        "guiness",
                        PageRequest.of(0, 10)
                );

        assertNotNull(actors);
        assertFalse(actors.isEmpty());
    }

    @Test
    void testSearchReturnsEmptyWhenNoMatch() {
        Page<Actor> actors =
                actorRepo.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                        "abcdef",
                        "ghijk",
                        PageRequest.of(0, 10)
                );

        assertNotNull(actors);
        assertTrue(actors.isEmpty());
    }

}
