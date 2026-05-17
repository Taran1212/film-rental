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
}