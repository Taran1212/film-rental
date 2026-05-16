package com.example.backend.repository;

import com.example.backend.entity.Film;
import com.example.backend.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    Long countByFilm(Film film);

    // Count inventory items for a specific film by filmId
    Long countByFilm_FilmId(Integer filmId);

    // Count inventory items for a specific film at a specific store
    Long countByFilm_FilmIdAndStoreId(Integer filmId, Integer storeId);

    // Get all inventory items for a store
    List<Inventory> findByStoreId(Integer storeId);

    // Get distinct films in inventory for a store (paginated)
    Page<Inventory> findByStoreId(Integer storeId, Pageable pageable);

    // Batch fetch inventory rows for many films in one query (avoids N+1 counts)
    List<Inventory> findByStoreIdAndFilm_FilmIdIn(Integer storeId, List<Integer> filmIds);
}
