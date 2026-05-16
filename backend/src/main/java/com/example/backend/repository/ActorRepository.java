package com.example.backend.repository;

import com.example.backend.dto.projection.ActorProjection;
import com.example.backend.entity.Actor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActorRepository extends JpaRepository<Actor,Integer> {
    @NonNull Page<Actor> findAll(@NonNull Pageable pageable);

    Page<ActorProjection> findAllProjectedBy(Pageable pageable);


    Page<ActorProjection> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName,
            String lastName,
            Pageable pageable
    );


    Page<ActorProjection> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(
            String firstName,
            String lastName,
            Pageable pageable
    );

    Optional<ActorProjection> findProjectedByActorId(Integer actorId);

    List<ActorProjection> findAllByOrderByFirstNameAscLastNameAsc();

    Optional<Actor> findTopByOrderByActorIdDesc();

}
