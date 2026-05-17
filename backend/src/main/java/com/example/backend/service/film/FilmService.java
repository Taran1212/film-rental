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
}
