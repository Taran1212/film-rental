package com.example.backend.controller;

import com.example.backend.controller.film.ActorController;

import com.example.backend.dto.projection.ActorProjection;
import com.example.backend.dto.projection.FilmProjection;
import com.example.backend.exception.GlobalExceptionHandler;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.security.CustomStaffDetailsService;
import com.example.backend.security.JwtService;
import com.example.backend.service.film.ActorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ActorController.class, excludeAutoConfiguration = {
        org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration.class,
        org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ActorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private CustomStaffDetailsService userDetailsService;
    @MockitoBean
    private ActorService actorService;

    private ActorProjection actor(int id, String name, long count) {
        return new ActorProjection() {
            public Integer getActorId() {
                return id;
            }

            public String getActorName() {
                return name;
            }

            public Long getTotalMovies() {
                return count;
            }
        };
    }

    private FilmProjection film(int id, String title) {
        return new FilmProjection() {
            public Integer getFilmId() { return id; }
            public String getTitle() { return title; }
            public String getReleaseYear() { return "2006"; }
            public String getLanguage() { return "English"; }
            public BigDecimal getRentalRate() { return new BigDecimal("0.99"); }
            public String getRating() { return "PG"; }
            public Integer getLength() { return 86; }
        };
    }

    @Test
    @DisplayName("GET /api/actors — returns paged actor projections")
    void shouldListActors() throws Exception {
        ActorProjection a1 = actor(1, "PENELOPE GUINESS", 19);
        ActorProjection a2 = actor(2, "NICK WAHLBERG", 26);
        when(actorService.getAllActors(any()))
                .thenReturn(new PageImpl<>(List.of(a1, a2), PageRequest.of(0, 5), 200));

        mockMvc.perform(get("/api/actors?page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].actorId").value(1))
                .andExpect(jsonPath("$.content[0].actorName").value("PENELOPE GUINESS"))
                .andExpect(jsonPath("$.content[0].totalMovies").value(19))
                .andExpect(jsonPath("$.content[1].actorName").value("NICK WAHLBERG"));
    }

    @Test
    @DisplayName("GET /api/actors/basic — returns flat list")
    void shouldReturnBasicList() throws Exception {
        when(actorService.getAllActorsBasic()).thenReturn(List.of(actor(1, "PENELOPE GUINESS", 19)));

        mockMvc.perform(get("/api/actors/basic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].actorId").value(1))
                .andExpect(jsonPath("$[0].actorName").value("PENELOPE GUINESS"));
    }

    @Test
    @DisplayName("GET /api/actors/search?name=… — delegates to searchActor")
    void shouldSearchActors() throws Exception {
        when(actorService.searchActor(eq("penelope"), any()))
                .thenReturn(new PageImpl<>(List.of(actor(1, "PENELOPE GUINESS", 19))));

        mockMvc.perform(get("/api/actors/search?name=penelope"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].actorName").value("PENELOPE GUINESS"));
    }

    @Test
    @DisplayName("GET /api/actors/{id} — returns single projection")
    void shouldReturnSingleActor() throws Exception {
        when(actorService.getActorById(1)).thenReturn(actor(1, "PENELOPE GUINESS", 19));

        mockMvc.perform(get("/api/actors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actorId").value(1))
                .andExpect(jsonPath("$.actorName").value("PENELOPE GUINESS"))
                .andExpect(jsonPath("$.totalMovies").value(19));
    }

    @Test
    @DisplayName("GET /api/actors/{id} — unknown id → 404 via ResourceNotFoundException")
    void shouldReturn404ForUnknownActor() throws Exception {
        when(actorService.getActorById(9999))
                .thenThrow(new ResourceNotFoundException("Actor not found"));

        mockMvc.perform(get("/api/actors/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Actor not found"));
    }

    @Test
    @DisplayName("GET /api/actors/{id} — non-numeric id → 400 type mismatch")
    void shouldReturn400ForBadId() throws Exception {
        mockMvc.perform(get("/api/actors/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("invalid value")));
    }


    @Test
    @DisplayName("GET /api/actors/{id}/movies — paginated films for actor")
    void shouldReturnActorMovies() throws Exception {
        when(actorService.getActorMovies(eq(1), any()))
                .thenReturn(new PageImpl<>(List.of(film(1, "ACADEMY DINOSAUR")), PageRequest.of(0, 10), 19));

        mockMvc.perform(get("/api/actors/1/movies?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("ACADEMY DINOSAUR"))
                .andExpect(jsonPath("$.content[0].language").value("English"));
    }

    @Test
    @DisplayName("GET /api/actors/{id}/movies — unknown actor → 404")
    void moviesShouldReturn404ForUnknownActor() throws Exception {
        when(actorService.getActorMovies(eq(9999), any()))
                .thenThrow(new ResourceNotFoundException("Actor not found"));

        mockMvc.perform(get("/api/actors/9999/movies"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Actor not found"));
    }
}