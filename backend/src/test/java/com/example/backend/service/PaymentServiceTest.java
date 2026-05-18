package com.example.backend.service;

import com.example.backend.entity.*;
import com.example.backend.repository.PaymentRepository;
import com.example.backend.service.payment.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Inventory inventoryWithRate(BigDecimal rate) {
        Film film = new Film();
        film.setRentalRate(rate);

        Inventory inv = new Inventory();
        inv.setInventoryId(1);
        inv.setFilm(film);
        return inv;
    }

    @Test
    @DisplayName("createPaymentForRental — should save payment with film's rental rate")
    void shouldSavePaymentWithFilmRate() {
        Rental rental = new Rental();
        rental.setRentalId(7);

        Customer customer = new Customer();
        customer.setCustomerId(2);

        Staff staff = new Staff();
        staff.setStaffId(3);

        Inventory inv = inventoryWithRate(BigDecimal.valueOf(4.99));

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Payment saved = paymentService.createPaymentForRental(rental, customer, staff, inv);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());
        Payment captured = captor.getValue();

        assertThat(captured.getRental()).isEqualTo(rental);
        assertThat(captured.getCustomer()).isEqualTo(customer);
        assertThat(captured.getStaff()).isEqualTo(staff);
        assertThat(captured.getAmount()).isEqualByComparingTo("4.99");
        assertThat(captured.getPaymentDate()).isNotNull();

        assertThat(saved).isSameAs(captured);
    }

    @Test
    @DisplayName("createPaymentForRental — should fall back to default amount when rate is null")
    void shouldFallBackToDefaultAmount() {
        Rental rental = new Rental();
        Customer customer = new Customer();
        Staff staff = new Staff();
        Inventory inv = inventoryWithRate(null);

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        paymentService.createPaymentForRental(rental, customer, staff, inv);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());

        assertThat(captor.getValue().getAmount()).isEqualByComparingTo("5.99");
    }
}
