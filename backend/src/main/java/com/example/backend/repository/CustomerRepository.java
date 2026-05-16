package com.example.backend.repository;

import com.example.backend.entity.Customer;
import com.example.backend.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Page<Customer> findByStore_storeIdAndFirstNameContainingIgnoreCase(
        Store store,
        String firstName,
        Pageable pageable
);

    Page<Customer> findByStore_storeIdAndLastNameContainingIgnoreCase(
            Store store,
            String lastName,
            Pageable pageable
    );


    Optional<Customer> findTopByOrderByCustomerIdDesc();


    Page<Customer> findByStore_storeId(Store store, Pageable pageable);


    Long countByStore_storeId(Store store);
}