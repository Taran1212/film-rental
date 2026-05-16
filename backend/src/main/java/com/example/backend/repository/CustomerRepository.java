package com.example.backend.repository;

import com.example.backend.entity.Customer;
import com.example.backend.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Page<Customer> findByStoreAndFirstNameContainingIgnoreCase(
        Store store,
        String firstName,
        Pageable pageable
);

    Page<Customer> findByStoreAndLastNameContainingIgnoreCase(
            Store store,
            String lastName,
            Pageable pageable
    );


    Optional<Customer> findTopByOrderByCustomerIdDesc();


    Page<Customer> findByStore(Store store, Pageable pageable);


    Long countByStore(Store store);
}