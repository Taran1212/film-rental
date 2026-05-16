package com.example.backend.dto.projection;

import org.springframework.beans.factory.annotation.Value;

public interface CountryProjection {
    Integer getCountryId();

    @Value("#{target.country}")
    String getName();
}
