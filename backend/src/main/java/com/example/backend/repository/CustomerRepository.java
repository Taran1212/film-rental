package com.example.backend.repository;

import com.example.backend.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    // Total customers in store
    Long countByStore_StoreId(Integer storeId);

    // All customers in store
    Page<Customer> findByStore_StoreId(Integer storeId, Pageable pageable);

    // Active customers count
    Long countByStore_StoreIdAndActive(Integer storeId, Integer active);

    // Active customers list
    Page<Customer> findByStore_StoreIdAndActive(
            Integer storeId,
            Integer active,
            Pageable pageable
    );

    // New customers count this month
    Long countByStore_StoreIdAndCreateDateBetween(
            Integer storeId,
            LocalDate startDate,
            LocalDate endDate
    );

    // New customers list this month
    Page<Customer> findByStore_StoreIdAndCreateDateBetween(
            Integer storeId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    // Search by first name
    Page<Customer> findByFirstNameContainingIgnoreCaseAndStore_StoreId(
            String firstName,
            Integer storeId,
            Pageable pageable
    );

    // Search by last name
    Page<Customer> findByLastNameContainingIgnoreCaseAndStore_StoreId(
            String lastName,
            Integer storeId,
            Pageable pageable
    );

    // Search by email
    Optional<Customer> findByEmailAndStore_StoreId(
            String email,
            Integer storeId
    );

    // Filter by active/inactive
    Page<Customer> findByActiveAndStore_StoreId(
            Integer active,
            Integer storeId,
            Pageable pageable
    );

    // Customer details
    Optional<Customer> findByCustomerId(Integer customerId);
}
