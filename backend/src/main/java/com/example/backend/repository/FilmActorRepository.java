package com.example.backend.repository;


import com.example.backend.entity.Actor;
import com.example.backend.entity.FilmActor;
import com.example.backend.entity.FilmActorId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmActorRepository
        extends JpaRepository<FilmActor, FilmActorId> {

    Long countByActor(Actor actor);

    Page<FilmActor>
    findByActor_FirstNameContainingIgnoreCaseOrActor_LastNameContainingIgnoreCase(
            String firstName,
            String lastName,
            Pageable pageable
    );
}