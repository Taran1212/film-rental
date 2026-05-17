package com.example.backend.repository;

import com.example.backend.dto.projection.CustomerProjection;
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

    Page<CustomerProjection>
    findProjectedByStore_StoreIdAndFirstNameContainingIgnoreCaseOrStore_StoreIdAndLastNameContainingIgnoreCase(
            Integer storeId1, String firstName,
            Integer storeId2, String lastName,
            Pageable pageable);

    Page<CustomerProjection>
    findProjectedByStore_StoreIdAndFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(
            Integer storeId, String firstName, String lastName,
            Pageable pageable);

    Page<CustomerProjection> findProjectedByStore_StoreId(Integer storeId, Pageable pageable);


    Customer findTopByOrderByCustomerIdDesc();

    Optional<CustomerProjection> findProjectedByCustomerId(Integer customerId);


    Page<Customer> findByStore_StoreId(Integer storeId, Pageable pageable);


    Long countByStore_StoreId(Integer storeId);

}