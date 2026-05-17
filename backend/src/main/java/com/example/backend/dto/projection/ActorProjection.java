package com.example.backend.dto.projection;

import org.springframework.beans.factory.annotation.Value;

public interface ActorProjection {

    Integer getActorId();

    @Value("#{target.firstName + ' ' + target.lastName}")
    String getActorName();

    @Value("#{target.filmActors.size()}")
    Long getTotalMovies();
}
