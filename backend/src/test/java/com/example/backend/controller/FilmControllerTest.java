package com.example.backend.controller;

import com.example.backend.controller.film.FilmController;

import tools.jackson.databind.ObjectMapper;
import com.example.backend.dto.projection.CategoryProjection;
import com.example.backend.dto.projection.FilmProjection;
import com.example.backend.dto.projection.LanguageProjection;
import com.example.backend.exception.GlobalExceptionHandler;
import com.example.backend.security.CustomStaffDetailsService;
import com.example.backend.security.JwtService;
import com.example.backend.service.film.FilmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FilmController.class, excludeAutoConfiguration = {
        org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration.class,
        org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper json;

    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private CustomStaffDetailsService userDetailsService;
    @MockitoBean
    private FilmService filmService;

    private FilmProjection film(int id, String title) {
        return new FilmProjection() {
            public Integer getFilmId() {
                return id;
            }

            public String getTitle() {
                return title;
            }

            public String getReleaseYear() {
                return "2006";
            }

            public String getLanguage() {
                return "English";
            }

            public BigDecimal getRentalRate() {
                return new BigDecimal("0.99");
            }

            public String getRating() {
                return "PG";
            }

            public Integer getLength() {
                return 86;
            }
        };
    }


    @Test
    @DisplayName("GET /api/movies — list returns projections")
    void shouldListMovies() throws Exception {
        when(filmService.getAllMovies(any()))
                .thenReturn(new PageImpl<>(List.of(film(1, "ACADEMY DINOSAUR"))));

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].filmId").value(1))
                .andExpect(jsonPath("$.content[0].title").value("ACADEMY DINOSAUR"))
                .andExpect(jsonPath("$.content[0].language").value("English"));
    }

    @Test
    @DisplayName("GET /api/movies/search?title=… delegates to searchMovies")
    void shouldSearchByTitle() throws Exception {
        when(filmService.searchMovies(any(), any()))
                .thenReturn(new PageImpl<>(List.of(film(1, "ACADEMY DINOSAUR"))));

        mockMvc.perform(get("/api/movies/search?title=academy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("ACADEMY DINOSAUR"));
    }
}
