package com.example.backend.service.payment;

import com.example.backend.entity.*;
import com.example.backend.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
    @Transactional
    public Payment createPaymentForRental(Rental rental, Customer customer, Staff staff, Inventory inventory) {
        Payment latestPayment = paymentRepository.findTopByOrderByPaymentIdDesc().orElse(null);
        int nextPaymentId = latestPayment == null ? 1 : latestPayment.getPaymentId() + 1;

        Payment payment = new Payment();
        payment.setPaymentId(nextPaymentId);
        payment.setCustomer(customer);
        payment.setStaff(staff);
        payment.setRental(rental);

        BigDecimal amount = inventory.getFilm().getRentalRate() != null
                ? inventory.getFilm().getRentalRate()
                : BigDecimal.valueOf(5.99);
        payment.setAmount(amount);
        payment.setPaymentDate(LocalDateTime.now());

        return paymentRepository.save(payment);
    }
}
