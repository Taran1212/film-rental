package com.example.backend.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class FilmActorId implements Serializable {

    private Integer actorId;
    private Integer filmId;
}