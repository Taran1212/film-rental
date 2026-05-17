package com.example.backend.dto.projection;

import org.springframework.beans.factory.annotation.Value;

public interface CityProjection {
    Integer getCityId();

    @Value("#{target.city}")
    String getName();

    @Value("#{target.country.countryId}")
    Integer getCountryId();
}
