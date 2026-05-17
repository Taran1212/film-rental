package com.example.backend.repository;

import com.example.backend.entity.Customer;
import com.example.backend.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Page<Customer> findByStore_StoreIdAndFirstNameContainingIgnoreCase(
        Integer storeId,
        String firstName,
        Pageable pageable
);

    Page<Customer> findByStore_StoreIdAndLastNameContainingIgnoreCase(
            Integer storeId,
            String lastName,
            Pageable pageable
    );


    Optional<Customer> findTopByOrderByCustomerIdDesc();


    Page<Customer> findByStore_StoreId(Integer storeId, Pageable pageable);


    Long countByStore_StoreId(Integer storeId);
    Long countByStoreId(Integer storeId);

}