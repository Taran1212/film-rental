package com.example.backend.Repository;

import com.example.backend.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Payment findTopByOrderByPaymentIdDesc();
}