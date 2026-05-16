package com.example.backend.repository;


import com.example.backend.entity.Film;
import com.example.backend.entity.Inventory;
import jakarta.transaction.Transactional;
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
@Transactional
class InventoryRepositoryTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Test
    @DisplayName("countByFilm_FilmId — non-negative count for an existing film")
    void shouldCountByFilmId() {
        Long count = inventoryRepository.countByFilm_FilmId(1);
        assertThat(count).isNotNull().isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("countByFilm_FilmId — unknown film returns zero")
    void zeroCountForUnknownFilm() {
        assertThat(inventoryRepository.countByFilm_FilmId(999_999)).isZero();
    }

    @Test
    @DisplayName("countByFilm_FilmIdAndStoreId — scoped count")
    void shouldCountByFilmAndStore() {
        Long count = inventoryRepository.countByFilm_FilmIdAndStoreId(1, 1);
        assertThat(count).isNotNull().isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("findByStoreId — list scoped to a store")
    void shouldListByStore() {
        List<Inventory> list = inventoryRepository.findByStoreId(1);
        assertThat(list).isNotEmpty().
        allSatisfy(i -> assertThat(i.getStoreId()).isEqualTo(1));
    }

    @Test
    @DisplayName("findByStoreIdAndFilm_FilmIdIn — batch fetch by film ids at a store")
    void shouldBatchFetchByFilmIds() {
        List<Inventory> list = inventoryRepository
                .findByStoreIdAndFilm_FilmIdIn(1, List.of(1, 2, 3));

        assertThat(list).isNotNull();
        assertThat(list).allSatisfy(i -> {
            assertThat(i.getStoreId()).isEqualTo(1);
            assertThat(i.getFilm()).isNotNull();
            assertThat(i.getFilm().getFilmId()).isIn(1, 2, 3);
        });
    }

    @Test
    @DisplayName("Inventory → Film + Store relations are navigable")
    void shouldNavigateRelations() {
        List<Inventory> list = inventoryRepository.findByStoreId(1);
        if (list.isEmpty()) return;

        Inventory first = list.get(0);
        assertThat(first.getFilm()).isNotNull();
        assertThat(first.getFilm().getFilmId()).isPositive();
        assertThat(first.getStore()).isNotNull();
        assertThat(first.getStore().getStoreId()).isEqualTo(1);
    }

    @Test
    @DisplayName("save — new inventory row gets a generated id (rolled back by @DataJpaTest)")
    void shouldSaveNewInventory() {
        Optional<Film> film = filmRepository.findById(1);
        assertThat(film).isPresent();

        Inventory inv = new Inventory();
        inv.setFilm(film.get());
        inv.setStoreId(1);
        inv.setLastUpdate(LocalDateTime.now());

        Inventory saved = inventoryRepository.save(inv);
        assertThat(saved).isNotNull();
        assertThat(saved.getInventoryId()).isNotNull().isPositive();
        assertThat(saved.getFilm().getFilmId()).isEqualTo(1);
        assertThat(saved.getStoreId()).isEqualTo(1);
        // tx rolls back at end — nothing persists
    }
}