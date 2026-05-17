package com.example.backend.controller.film;

import com.example.backend.dto.projection.CityProjection;
import com.example.backend.dto.projection.CountryProjection;
import com.example.backend.repository.CityRepository;
import com.example.backend.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {
    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;

    @GetMapping("/countries")
    public List<CountryProjection> getCountries() {
        return countryRepository.findAllByOrderByCountryAsc();
    }

    @GetMapping("/cities")
    public List<CityProjection> getCities(@RequestParam Integer countryId) {
        return cityRepository.findByCountryCountryIdOrderByCityAsc(countryId);
    }
}
