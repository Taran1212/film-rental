package com.example.backend.repository;

import com.example.backend.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository
        extends JpaRepository<Staff, Integer> {

    Optional<Staff> findByUsername(String username);

    Optional<Staff> findTopByOrderByStaffIdDesc();

    List<Staff> findByStoreId(
            Integer storeId
    );
}