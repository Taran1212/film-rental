package com.example.backend.service;

import com.example.backend.dto.InventoryDto;
import com.example.backend.entity.Film;
import com.example.backend.entity.Staff;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.*;
import com.example.backend.util.AuthUtil;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


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


}
