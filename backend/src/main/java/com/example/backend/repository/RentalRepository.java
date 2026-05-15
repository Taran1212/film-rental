package com.example.backend.repository;

import com.example.backend.entity.Inventory;
import com.example.backend.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental,Integer> {

    Long countByStaff_StoreIdAndReturnDateIsNull(Integer storeId);

    boolean existsByInventoryAndReturnDateIsNull(Inventory inventory);

    Long countByReturnDateIsNull();

    Rental findTopByOrderByRentalIdDesc();

    Long countByInventory_Film_FilmIdAndReturnDateIsNull(Integer filmId);

    Long countByInventory_Film_FilmIdAndInventory_StoreIdAndReturnDateIsNull(Integer filmId, Integer storeId);

    List<Rental> findByInventory_StoreIdAndInventory_Film_FilmIdInAndReturnDateIsNull(
            Integer storeId, List<Integer> filmIds);

}
