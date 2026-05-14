package com.example.backend.repository;

import com.example.backend.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Payment findTopByOrderByPaymentIdDesc();
}