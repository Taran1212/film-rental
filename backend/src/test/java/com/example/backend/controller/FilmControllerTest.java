package com.example.backend.controller;

import com.example.backend.controller.film.FilmController;

import com.example.backend.dto.MovieDetailsDto;
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


    @Test
    @DisplayName("GET /api/movies/actor?name=… delegates to searchMoviesByActor")
    void shouldSearchByActor() throws Exception {
        when(filmService.searchMoviesByActor(any(), any()))
                .thenReturn(new PageImpl<>(List.of(film(1, "ACADEMY DINOSAUR"))));

        mockMvc.perform(get("/api/movies/actor?name=penelope"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /api/movies/category?name=… delegates to getMoviesByCategory")
    void shouldGetByCategory() throws Exception {
        when(filmService.getMoviesByCategory(any(), any()))
                .thenReturn(new PageImpl<>(List.of(film(1, "ACADEMY DINOSAUR"))));

        mockMvc.perform(get("/api/movies/category?name=action"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /api/movies/{id}/details — returns enhanced detail DTO with cast actorId+name")
    void shouldReturnDetails() throws Exception {
        MovieDetailsDto details = MovieDetailsDto.builder()
                .filmId(1).title("ACADEMY DINOSAUR").description("A film")
                .language("English").rating("PG").length(86)
                .rentalRate(new BigDecimal("0.99"))
                .actors(List.of(new MovieDetailsDto.ActorSummary(1, "PENELOPE GUINESS")))
                .categories(List.of("Action"))
                .build();
        when(filmService.getMovieDetails(1)).thenReturn(details);

        mockMvc.perform(get("/api/movies/1/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("ACADEMY DINOSAUR"))
                .andExpect(jsonPath("$.actors[0].actorId").value(1))
                .andExpect(jsonPath("$.actors[0].name").value("PENELOPE GUINESS"))
                .andExpect(jsonPath("$.categories[0]").value("Action"));
    }

    @Test
    @DisplayName("GET /api/movies/languages — list of LanguageProjection")
    void shouldListLanguages() throws Exception {
        LanguageProjection lang = new LanguageProjection() {
            public Integer getLanguageId() { return 1; }
            public String getName() { return "English"; }
        };
        when(filmService.getAllLanguages()).thenReturn(List.of(lang));

        mockMvc.perform(get("/api/movies/languages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].languageId").value(1))
                .andExpect(jsonPath("$[0].name").value("English"));
    }

    @Test
    @DisplayName("GET /api/movies/categories — list of CategoryProjection")
    void shouldListCategories() throws Exception {
        CategoryProjection c = new CategoryProjection() {
            public Integer getCategoryId() { return 1; }
            public String getName() { return "Action"; }
        };
        when(filmService.getAllCategories()).thenReturn(List.of(c));

        mockMvc.perform(get("/api/movies/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Action"));
    }


    @Test
    @DisplayName("POST /api/movies — empty body → 400 with required-field errors")
    void shouldRejectEmptyBody() throws Exception {
        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.title").exists())
                .andExpect(jsonPath("$.fieldErrors.languageId").exists())
                .andExpect(jsonPath("$.fieldErrors.rentalRate").exists());
    }

}
