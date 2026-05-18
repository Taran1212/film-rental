package com.example.backend.controller.film;

import com.example.backend.dto.*;
import com.example.backend.dto.projection.CategoryProjection;
import com.example.backend.dto.projection.FilmProjection;
import com.example.backend.dto.projection.LanguageProjection;
import com.example.backend.service.film.FilmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STAFF')")
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Page<FilmProjection> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return filmService.getAllMovies(PageRequest.of(page, size));
    }

    @GetMapping("/search")
    public Page<FilmProjection> searchMovies(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return filmService.searchMovies(title, PageRequest.of(page, size));
    }

    @GetMapping("/actor")
    public Page<FilmProjection> searchMoviesByActor(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return filmService.searchMoviesByActor(name, PageRequest.of(page, size));
    }

    @GetMapping("/category")
    public Page<FilmProjection> getMoviesByCategory(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return filmService.getMoviesByCategory(name, PageRequest.of(page, size));
    }

    @GetMapping("/{id}/details")
    public MovieDetailsDto getMovieDetails(@PathVariable Integer id) {
        return filmService.getMovieDetails(id);
    }

    @GetMapping("/languages")
    public List<LanguageProjection> getAllLanguages() {
        return filmService.getAllLanguages();
    }

    @GetMapping("/categories")
    public List<CategoryProjection> getAllCategories() {
        return filmService.getAllCategories();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public java.util.Map<String, Integer> createMovie(@Valid @RequestBody MovieCreateRequestDto request) {
        Integer id = filmService.createMovie(request);
        return java.util.Map.of("filmId", id);
    }

}