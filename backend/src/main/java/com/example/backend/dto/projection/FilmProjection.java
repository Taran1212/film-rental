package com.example.backend.dto.projection;

import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

public interface FilmProjection {

    Integer getFilmId();

    String getTitle();

    String getReleaseYear();

    @Value("#{target.language?.name}")
    String getLanguage();

    BigDecimal getRentalRate();

    String getRating();

    Integer getLength();
}
