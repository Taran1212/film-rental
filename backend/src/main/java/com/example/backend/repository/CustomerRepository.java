package com.example.backend.repository;

import com.example.backend.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository
        extends JpaRepository<Customer, Integer> {

    // Search by first name OR last name, scoped to a store
    Page<Customer> findByStoreIdAndFirstNameContainingIgnoreCaseOrStoreIdAndLastNameContainingIgnoreCase(
            Integer storeId1, String firstName,
            Integer storeId2, String lastName,
            Pageable pageable
    );

    // Full name search: firstName matches first part AND lastName matches second part, scoped to store
    Page<Customer> findByStoreIdAndFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(
            Integer storeId, String firstName, String lastName,
            Pageable pageable
    );

    Customer findTopByOrderByCustomerIdDesc();

    Page<Customer> findByStoreId(
            Integer storeId,
            Pageable pageable
    );

    Long countByStoreId(Integer storeId);
}