package com.example.backend.service.film;

import com.example.backend.dto.*;
import com.example.backend.dto.projection.CategoryProjection;
import com.example.backend.dto.projection.FilmProjection;
import com.example.backend.dto.projection.LanguageProjection;
import com.example.backend.entity.*;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.*;
import com.example.backend.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FilmService {

    private final FilmRepository filmRepository;
    private final FilmActorRepository filmActorRepository;
    private final FilmCategoryRepository filmCategoryRepository;
    private final InventoryRepository inventoryRepository;
    private final StaffRepository staffRepository;
    private final LanguageRepository languageRepository;
    private final CategoryRepository categoryRepository;
    private final ActorRepository actorRepository;
    private final AuthUtil authUtil;


    public Page<FilmProjection> getAllMovies(Pageable pageable) {
        return filmRepository.findAllProjectedBy(pageable);
    }

    public Page<FilmProjection> searchMovies(String title, Pageable pageable) {
        return filmRepository.findByTitleContainingIgnoreCase(title, pageable);
    }


    public Page<FilmProjection> searchMoviesByActor(String actorName, Pageable pageable) {
        String trimmed = actorName.trim();
        String[] parts = trimmed.split("\\s+");

        if (parts.length >= 2) {
            return filmRepository
                    .findDistinctByFilmActors_Actor_FirstNameContainingIgnoreCaseAndFilmActors_Actor_LastNameContainingIgnoreCase(
                            parts[0], parts[parts.length - 1], pageable);
        }
        return filmRepository
                .findDistinctByFilmActors_Actor_FirstNameContainingIgnoreCaseOrFilmActors_Actor_LastNameContainingIgnoreCase(
                        trimmed, trimmed, pageable);
    }

    public Page<FilmProjection> getMoviesByCategory(String categoryName, Pageable pageable) {
        return filmRepository.findDistinctByFilmCategories_Category_NameIgnoreCase(categoryName, pageable);
    }

    // ===== Languages / Categories (interface projections) =====

    public List<LanguageProjection> getAllLanguages() {
        return languageRepository.findAllByOrderByNameAsc();
    }

    public List<CategoryProjection> getAllCategories() {
        return categoryRepository.findAllByOrderByNameAsc();
    }


    public MovieDetailsDto getMovieDetails(Integer filmId) {
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        List<MovieDetailsDto.ActorSummary> actors = film.getFilmActors().stream()
                .map(fa -> new MovieDetailsDto.ActorSummary(
                        fa.getActor().getActorId(),
                        fa.getActor().getFirstName() + " " + fa.getActor().getLastName()))
                .toList();

        List<String> categories = film.getFilmCategories().stream()
                .map(fc -> fc.getCategory().getName())
                .toList();

        return MovieDetailsDto.builder()
                .filmId(film.getFilmId())
                .title(film.getTitle())
                .description(film.getDescription())
                .releaseYear(String.valueOf(film.getReleaseYear()))
                .language(film.getLanguage().getName())
                .rating(film.getRating())
                .length(film.getLength())
                .rentalDuration(film.getRentalDuration())
                .rentalRate(film.getRentalRate())
                .replacementCost(film.getReplacementCost())
                .specialFeatures(film.getSpecialFeatures())
                .actors(actors)
                .categories(categories)
                .build();
    }

    private void linkActorToFilm(Actor actor, Film film) {
        FilmActorId id = new FilmActorId();
        id.setActorId(actor.getActorId());
        id.setFilmId(film.getFilmId());
        FilmActor fa = new FilmActor();
        fa.setId(id);
        fa.setActor(actor);
        fa.setFilm(film);
        fa.setLastUpdate(LocalDateTime.now());
        filmActorRepository.save(fa);
    }
}
