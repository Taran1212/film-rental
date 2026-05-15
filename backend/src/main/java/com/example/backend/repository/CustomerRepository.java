package com.example.backend.repository;

import com.example.backend.entity.Customer;
import com.example.backend.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository
        extends JpaRepository<Customer, Integer> {

    // Search by first name OR last name, scoped to a store
    Page<Customer> findByStoreIdAndFirstNameContainingIgnoreCaseOrStoreIdAndLastNameContainingIgnoreCase(
            Store storeId1,
            String firstName,
            Store storeId2,
            String lastName,
            Pageable pageable
    );

    // Full name search: firstName matches first part AND lastName matches second part, scoped to store
    Page<Customer> findByStoreIdAndFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(
            Store storeId,
            String firstName,
            String lastName,
            Pageable pageable
    );

    Optional<Customer> findTopByOrderByCustomerIdDesc();

    Page<Customer> findByStoreId(Store storeId, Pageable pageable);

    Long countByStoreId(Store storeId);
}