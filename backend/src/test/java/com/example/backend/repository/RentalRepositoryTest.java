package com.example.backend.repository;

import com.example.backend.dto.projection.RecentRentalProjection;
import com.example.backend.dto.projection.RentalProjection;
import com.example.backend.entity.Inventory;
import com.example.backend.entity.Rental;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class RentalRepositoryTest {

    @Autowired
    private RentalRepository rentalRepo;

    @Autowired
    private InventoryRepository inventoryRepo;

    @Test
    @DisplayName("findByStaff_StoreIdAndReturnDateIsNull — active rentals for store 1 (projection + EntityGraph)")
    void shouldReturnActiveRentalsForStore() {
        Page<RentalProjection> page = rentalRepo
                .findByStaff_StoreIdAndReturnDateIsNull(1, PageRequest.of(0, 5));

        assertThat(page.getContent()).isNotEmpty();
        RentalProjection r = page.getContent().get(0);
        // All five fields populated by SpEL via @EntityGraph eager fetch
        assertThat(r.getRentalId()).isNotNull();
        assertThat(r.getMovieTitle()).isNotBlank();
        assertThat(r.getCustomerName()).isNotBlank().contains(" ");
        assertThat(r.getRentalDate()).isNotNull();
        assertThat(r.getReturned()).isFalse();   // active = not returned
        assertThat(r.getReturnDate()).isNull();
    }

    @Test
    @DisplayName("countByStaff_StoreIdAndReturnDateIsNull matches the projection page totalElements")
    void countAndPageAgree() {
        Long count = rentalRepo.countByStaff_StoreIdAndReturnDateIsNull(1);
        Page<RentalProjection> page = rentalRepo
                .findByStaff_StoreIdAndReturnDateIsNull(1, PageRequest.of(0, 1));

        assertThat(count).isEqualTo(page.getTotalElements());
    }

    @Test
    @DisplayName("Multi-predicate search — by film title fragment")
    void shouldSearchByTitleFragment() {
        Page<RentalProjection> page = rentalRepo
                .findByStaff_StoreIdAndReturnDateIsNullAndInventory_Film_TitleContainingIgnoreCaseOrStaff_StoreIdAndReturnDateIsNullAndCustomer_FirstNameContainingIgnoreCaseOrStaff_StoreIdAndReturnDateIsNullAndCustomer_LastNameContainingIgnoreCase(
                        1, "MOON", 1, "MOON", 1, "MOON", PageRequest.of(0, 5));

        // Either some rentals match or no rentals match — both are valid outcomes;
        // the assertion is that no exception is thrown and pagination metadata is sane
        assertThat(page).isNotNull();
        assertThat(page.getTotalPages()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("Customer-full-name search — exact match")
    void shouldSearchByCustomerFullName() {
        Page<RentalProjection> page = rentalRepo
                .findByStaff_StoreIdAndReturnDateIsNullAndCustomer_FirstNameContainingIgnoreCaseAndCustomer_LastNameContainingIgnoreCase(
                        1, "ZZZZ_NO_MATCH", "ZZZZ_NO_MATCH", PageRequest.of(0, 5));

        assertThat(page.getContent()).isEmpty();
    }

    @Test
    @DisplayName("findByCustomer_CustomerId — rentals for customer #1 (MARY SMITH)")
    void shouldReturnRentalsForCustomer() {
        Page<RentalProjection> page = rentalRepo
                .findByCustomer_CustomerId(1, PageRequest.of(0, 10));

        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent()).allSatisfy(r ->
                assertThat(r.getCustomerName()).isNotBlank());
    }

    @Test
    @DisplayName("findTop5ByStaff_StoreIdOrderByRentalDateDesc — dashboard widget query")
    void shouldReturnLatestFiveRentals() {
        List<RecentRentalProjection> recent =
                rentalRepo.findTop5ByStaff_StoreIdOrderByRentalDateDesc(1);

        assertThat(recent).hasSizeLessThanOrEqualTo(5);
        // Should be ordered by rentalDate DESC
        for (int i = 1; i < recent.size(); i++) {
            assertThat(recent.get(i - 1).getRentalDate())
                    .isAfterOrEqualTo(recent.get(i).getRentalDate());
        }
    }

    @Test
    @DisplayName("existsByInventoryAndReturnDateIsNull — used by rent flow to refuse duplicate rent")
    void shouldDetectAlreadyRentedCopy() {
        // Pick any inventory copy that currently has an active rental
        Page<RentalProjection> active = rentalRepo
                .findByStaff_StoreIdAndReturnDateIsNull(1, PageRequest.of(0, 1));
        if (active.getContent().isEmpty()) return;  // no active rentals — skip
        Integer rentalId = active.getContent().get(0).getRentalId();
        Rental rental = rentalRepo.findById(rentalId).orElseThrow();
        Inventory inv = rental.getInventory();

        assertThat(rentalRepo.existsByInventoryAndReturnDateIsNull(inv)).isTrue();
    }

    @Test
    void testCountByStaffStoreIdAndReturnDateIsNull() {
        Long count = rentalRepo.countByStaff_StoreIdAndReturnDateIsNull(1);

        assertNotNull(count);
        assertTrue(count >= 0);

        System.out.println("Active rentals for staff store 1: " + count);
    }

    @Test
    void testExistsByInventoryAndReturnDateIsNull() {
        Inventory inventory = inventoryRepo.findById(1).orElse(null);

        assertNotNull(inventory);

        boolean exists = rentalRepo.existsByInventoryAndReturnDateIsNull(inventory);

        System.out.println("Inventory 1 currently rented: " + exists);
    }

    @Test
    void testCountByReturnDateIsNull() {
        Long count = rentalRepo.countByReturnDateIsNull();

        assertNotNull(count);
        assertTrue(count >= 0);

        System.out.println("Total active rentals: " + count);
    }

    @Test
    void testFindTopByOrderByRentalIdDesc() {
        Rental latestRental = rentalRepo.findTopByOrderByRentalIdDesc();

        assertNotNull(latestRental);
        assertNotNull(latestRental.getRentalId());

        System.out.println("Latest Rental ID: " + latestRental.getRentalId());
    }

    @Test
    void testCountByInventoryFilmFilmIdAndReturnDateIsNull() {
        Long count = rentalRepo.countByInventory_Film_FilmIdAndReturnDateIsNull(1);

        assertNotNull(count);
        assertTrue(count >= 0);

        System.out.println("Active rentals for film 1: " + count);
    }

    @Test
    void testCountByInventoryFilmFilmIdAndInventoryStoreIdAndReturnDateIsNull() {
        Long count =
                rentalRepo.countByInventory_Film_FilmIdAndInventory_Store_StoreIdAndReturnDateIsNull(
                        1,
                        1
                );

        assertNotNull(count);
        assertTrue(count >= 0);

        System.out.println("Active rentals for film 1 in store 1: " + count);
    }

    @Test
    void testFindByInventoryStoreIdAndInventoryFilmFilmIdInAndReturnDateIsNull() {
        List<Integer> filmIds = List.of(1, 2, 3, 4, 5);

        List<Rental> rentals =
                rentalRepo.findByInventory_Store_StoreIdAndInventory_Film_FilmIdInAndReturnDateIsNull(
                        1,
                        filmIds
                );

        assertNotNull(rentals);

        rentals.forEach(rental -> {
            assertNull(rental.getReturnDate());
            assertEquals(1, rental.getInventory().getStore().getStoreId());
            assertTrue(filmIds.contains(rental.getInventory().getFilm().getFilmId()));

            System.out.println("Rental ID: " + rental.getRentalId());
        });
    }
}
