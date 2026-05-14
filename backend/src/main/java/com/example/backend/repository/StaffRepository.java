package com.example.backend.repository;

import com.example.backend.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StaffRepository
        extends JpaRepository<Staff, Integer> {

    Optional<Staff> findByUsername(String username);

    Optional<Staff> findTopByOrderByStaffIdDesc();

    List<Staff> findByStoreId(
            Integer storeId
    );
}