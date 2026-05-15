package com.example.backend.repository;
import com.example.backend.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StoreRepository
        extends JpaRepository<Store, Integer> {

    Optional<Store> findTopByOrderByStoreIdDesc();
}