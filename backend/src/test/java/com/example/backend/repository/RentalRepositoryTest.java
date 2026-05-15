package com.example.backend.repository;

import com.example.backend.entity.Inventory;
import com.example.backend.entity.Rental;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RentalRepositoryTest {

    @Autowired
    private RentalRepository rentalRepo;

    @Autowired
    private InventoryRepository inventoryRepo;

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
                rentalRepo.countByInventory_Film_FilmIdAndInventory_StoreIdAndReturnDateIsNull(
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
                rentalRepo.findByInventory_StoreIdAndInventory_Film_FilmIdInAndReturnDateIsNull(
                        1,
                        filmIds
                );

        assertNotNull(rentals);

        rentals.forEach(rental -> {
            assertNull(rental.getReturnDate());
            assertEquals(1, rental.getInventory().getStoreId());
            assertTrue(filmIds.contains(rental.getInventory().getFilm().getFilmId()));

            System.out.println("Rental ID: " + rental.getRentalId());
        });
    }
}
