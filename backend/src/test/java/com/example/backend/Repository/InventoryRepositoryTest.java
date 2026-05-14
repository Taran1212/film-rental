package com.example.backend.repository;

import com.example.backend.entity.Film;
import com.example.backend.entity.Inventory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class InventoryRepositoryTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Test
    @DisplayName("Should count inventory by film ID")
    void shouldCountByFilmFilmId() {
        // Get an existing film from the database
        Optional<Film> film = filmRepository.findById(1);

        assertThat(film).isPresent();

        // Count inventory items for this film
        Long count = inventoryRepository.countByFilmFilmId(film.get().getFilmId());

        assertThat(count).isNotNull().isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("Should return zero count for non-existent film ID")
    void shouldReturnZeroCountForNonExistentFilm() {
        Long count = inventoryRepository.countByFilmFilmId(999999);

        assertThat(count).isZero();
    }

    @Test
    @DisplayName("Should find all inventory items")
    void shouldFindAllInventory() {
        List<Inventory> inventories = inventoryRepository.findAll();

        assertThat(inventories).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("Should find inventory by ID")
    void shouldFindInventoryById() {
        Optional<Inventory> inventory = inventoryRepository.findById(1);

        assertThat(inventory).isPresent();
        assertThat(inventory.get().getInventoryId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return empty optional for non-existent inventory ID")
    void shouldReturnEmptyForNonExistentId() {
        Optional<Inventory> inventory = inventoryRepository.findById(999999);

        assertThat(inventory).isEmpty();
    }

    @Test
    @DisplayName("Should save new inventory item")
    void shouldSaveNewInventory() {
        // Get an existing film
        Optional<Film> film = filmRepository.findById(1);
        assertThat(film).isPresent();

        // Create a new inventory
        Inventory inventory = new Inventory();
        inventory.setFilm(film.get());
        inventory.setStoreId(1);
        inventory.setLastUpdate(LocalDateTime.now());

        Inventory savedInventory = inventoryRepository.save(inventory);

        assertThat(savedInventory).isNotNull();
        assertThat(savedInventory.getInventoryId()).isNotNull();
        assertThat(savedInventory.getFilm().getFilmId()).isEqualTo(1);
        assertThat(savedInventory.getStoreId()).isEqualTo(1);

    }
}