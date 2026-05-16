package com.example.backend.repository;

import com.example.backend.dto.projection.RecentRentalProjection;
import com.example.backend.dto.projection.RentalProjection;
import com.example.backend.entity.Inventory;
import com.example.backend.entity.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental,Integer> {

    @EntityGraph(attributePaths = {"inventory.film", "customer"})
    Page<RentalProjection> findByStaff_StoreIdAndReturnDateIsNull(
            Integer storeId, Pageable pageable);

    @EntityGraph(attributePaths = {"inventory.film", "customer"})
    Page<RentalProjection>
    findByStaff_StoreIdAndReturnDateIsNullAndInventory_Film_TitleContainingIgnoreCaseOrStaff_StoreIdAndReturnDateIsNullAndCustomer_FirstNameContainingIgnoreCaseOrStaff_StoreIdAndReturnDateIsNullAndCustomer_LastNameContainingIgnoreCase(
            Integer storeId1, String title,
            Integer storeId2, String firstName,
            Integer storeId3, String lastName,
            Pageable pageable);

    @EntityGraph(attributePaths = {"inventory.film", "customer"})
    Page<RentalProjection>
    findByStaff_StoreIdAndReturnDateIsNullAndCustomer_FirstNameContainingIgnoreCaseAndCustomer_LastNameContainingIgnoreCase(
            Integer storeId, String firstName, String lastName, Pageable pageable);

    @EntityGraph(attributePaths = {"inventory.film", "customer"})
    Page<RentalProjection> findByCustomer_CustomerId(Integer customerId, Pageable pageable);

    @EntityGraph(attributePaths = {"inventory.film", "customer"})
    List<RecentRentalProjection> findTop5ByStaff_StoreIdOrderByRentalDateDesc(Integer storeId);




    Long countByStaff_StoreIdAndReturnDateIsNull(Integer storeId);

    boolean existsByInventoryAndReturnDateIsNull(Inventory inventory);

    Long countByReturnDateIsNull();

    Rental findTopByOrderByRentalIdDesc();

    Long countByInventory_Film_FilmIdAndReturnDateIsNull(Integer filmId);

    Long countByInventory_Film_FilmIdAndInventory_Store_StoreIdAndReturnDateIsNull(Integer filmId, Integer storeId);

    List<Rental> findByInventory_Store_StoreIdAndInventory_Film_FilmIdInAndReturnDateIsNull(
            Integer storeId, List<Integer> filmIds);


}
