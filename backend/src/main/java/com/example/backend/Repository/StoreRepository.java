package com.example.backend.repository;


import com.example.backend.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository
        extends JpaRepository<Store, Integer> {

    Store findTopByOrderByStoreIdDesc();
}