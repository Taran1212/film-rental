package com.example.backend.repository;

import com.example.backend.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Optional<Payment> findTopByOrderByPaymentIdDesc();
    Page<Payment> findByStaff_StoreId(Integer storeId, Pageable pageable);
}