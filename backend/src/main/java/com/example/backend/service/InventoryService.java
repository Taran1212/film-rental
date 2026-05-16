package com.example.backend.service;

import com.example.backend.dto.InventoryDto;
import com.example.backend.entity.Film;
import com.example.backend.entity.Staff;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.*;
import com.example.backend.util.AuthUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final FilmRepository filmRepository;
    private final StoreRepository storeRepository;
    private final AuthUtil authUtil;
    private final RentalRepository rentalRepository;
    private final StaffRepository staffRepository;


    // utility method
    private Integer currentStoreId() {
        String username = authUtil.getLoggedInUsername();
        Staff staff = staffRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Staff not found with username: " + username));
        return staff.getStore().getStoreId();
    }


    public InventoryDto getInventory(Integer filmId) {
        Film film = inventoryRepository.findById(filmId).orElseThrow(() -> new ResourceNotFoundException("film not found with id: " + filmId)).getFilm();
        Integer storeId = currentStoreId();

        Long totalCopies = inventoryRepository.countByFilm_FilmIdAndStore_StoreId(filmId, storeId);
        Long rentedCopies = rentalRepository.countByInventory_Film_FilmIdAndInventory_Store_StoreIdAndReturnDateIsNull(
                filmId, storeId
        );

        return InventoryDto.builder()
                .filmId(filmId)
                .movieTitle(film.getTitle())
                .totalCopies(totalCopies)
                .rentedCopies(rentedCopies)
                .availableCopies(totalCopies - rentedCopies)
                .build();
    }

    public Page<InventoryDto> getStoreInventory(Pageable pageable) {
        return getStoreInventory(null, pageable);
    }

    public Page<InventoryDto> getStoreInventory(String search, Pageable pageable) {
        Integer storeId = currentStoreId();
        Page<Film> filmPage;

        if (search != null && !search.isBlank()) {
            filmPage = filmRepository.findDistinctByInventories_Store_StoreIdAndTitleContainingIgnoreCase(
                    storeId, search.trim(), pageable
            );
        } else {
            filmPage = filmRepository.findDistinctByInventories_Store_StoreId(storeId, pageable);
        }

        List<Integer> filmIds = filmPage.getContent().stream().map(Film::getFilmId).toList();

        if (filmIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, filmPage.getTotalElements());
        }

        Map<Integer, Long> totalByFilm = inventoryRepository.findByStore_StoreIdAndFilm_FilmIdIn(storeId, filmIds).stream()
                .collect(Collectors.groupingBy(i -> i.getFilm().getFilmId(), Collectors.counting()));

        Map<Integer, Long> rentedByFilm = rentalRepository.findByInventory_Store_StoreIdAndInventory_Film_FilmIdInAndReturnDateIsNull(
                        storeId, filmIds)
                .stream()
                .collect(
                        Collectors.groupingBy
                                (r -> r.getInventory().getFilm().getFilmId(),
                                        Collectors.counting())
                );

        List<InventoryDto> inventoryDtos = filmPage.getContent().stream()
                .map(film -> {
                    Integer filmId = film.getFilmId();
                    long total = totalByFilm.getOrDefault(filmId, 0L);
                    long rented = rentedByFilm.getOrDefault(filmId, 0L);
                    return InventoryDto.builder()
                            .filmId(filmId)
                            .movieTitle(film.getTitle())
                            .totalCopies(total)
                            .rentedCopies(rented)
                            .availableCopies(total - rented)
                            .build();
                })
                .toList();
        return new PageImpl<>(inventoryDtos, pageable, filmPage.getTotalElements());
    }

    

}
