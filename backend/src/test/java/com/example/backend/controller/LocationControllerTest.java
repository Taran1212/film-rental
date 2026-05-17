package com.example.backend.controller;

import com.example.backend.controller.film.LocationController;
import com.example.backend.dto.projection.CityProjection;
import com.example.backend.dto.projection.CountryProjection;
import com.example.backend.exception.GlobalExceptionHandler;
import com.example.backend.repository.CityRepository;
import com.example.backend.repository.CountryRepository;
import com.example.backend.security.CustomStaffDetailsService;
import com.example.backend.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = LocationController.class, excludeAutoConfiguration = {
        org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration.class,
        org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class LocationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;
    @MockitoBean private CustomStaffDetailsService userDetailsService;
    @MockitoBean private CountryRepository countryRepository;
    @MockitoBean private CityRepository cityRepository;

    private CountryProjection country(int id, String name) {
        return new CountryProjection() {
            public Integer getCountryId() { return id; }
            public String getName() { return name; }
        };
    }

    private CityProjection city(int id, String name, int countryId) {
        return new CityProjection() {
            public Integer getCityId() { return id; }
            public String getName() { return name; }
            public Integer getCountryId() { return countryId; }
        };
    }

    @Test
    @DisplayName("GET /api/locations/countries — projection list")
    void shouldListCountries() throws Exception {
        when(countryRepository.findAllByOrderByCountryAsc())
                .thenReturn(List.of(country(1, "Afghanistan"), country(2, "Algeria")));

        mockMvc.perform(get("/api/locations/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].countryId").value(1))
                .andExpect(jsonPath("$[0].name").value("Afghanistan"))
                .andExpect(jsonPath("$[1].name").value("Algeria"));
    }

    @Test
    @DisplayName("GET /api/locations/cities?countryId=44 — projection list scoped to country")
    void shouldListCitiesForCountry() throws Exception {
        when(cityRepository.findByCountry_CountryIdOrderByCityAsc(44))
                .thenReturn(List.of(city(8, "Adoni", 44), city(20, "Bhopal", 44)));

        mockMvc.perform(get("/api/locations/cities?countryId=44"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cityId").value(8))
                .andExpect(jsonPath("$[0].name").value("Adoni"))
                .andExpect(jsonPath("$[0].countryId").value(44));
    }

    @Test
    @DisplayName("GET /api/locations/cities — missing countryId → 400")
    void shouldRejectMissingCountryId() throws Exception {
        mockMvc.perform(get("/api/locations/cities"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("countryId")));
    }
}
