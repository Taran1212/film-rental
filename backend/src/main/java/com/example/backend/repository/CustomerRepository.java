package com.example.backend.repository;

import com.example.backend.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Total customers in store
    //Long countByStoreStoreId(Long storeId);

    // All customers in store
    //Page<Customer> findByStoreStoreId(Long storeId, Pageable pageable);

    // Active customers count
  //  Long countByStoreStoreIdAndActive(Long storeId, Long active);

    // Active customers list
//    Page<Customer> findByStoreStoreIdAndActive(
//            Long storeId,
//            Long active,
//            Pageable pageable
//    );

    // New customers count this month
//    Long countByStoreStoreIdAndCreateDateBetween(
//            Long storeId,
//            LocalDate startDate,
//            LocalDate endDate
//    );

    // New customers list this month
//    Page<Customer> findByStoreStoreIdAndCreateDateBetween(
//            Integer storeId,
//            LocalDate startDate,
//            LocalDate endDate,
//            Pageable pageable
//    );

    // Search by first name
//    Page<Customer> findByFirstNameContainingIgnoreCaseAndStoreStoreId(
//            String firstName,
//            Long storeId,
//            Pageable pageable
//    );

    // Search by last name
//    Page<Customer> findByLastNameContainingIgnoreCaseAndStoreStoreId(
//            String lastName,
//            Long storeId,
//            Pageable pageable
//    );

    // Search by email
//    Optional<Customer> findByEmailAndStoreStoreId(
//            String email,
//            Long storeId
//    );

    // Filter by active/inactive
//    Page<Customer> findByActiveAndStoreStoreId(
//            Long active,
//            Long storeId,
//            Pageable pageable
//    );

    // Customer details
    Optional<Customer> findByCustomerId(Long customerId);
}
