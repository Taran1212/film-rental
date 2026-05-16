
package com.example.backend.repository;

import com.example.backend.entity.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RentalRepository rentalRepository;

    private static final Integer STAFF1_ID   = 1;
    private static final Integer STAFF2_ID   = 2;
    private static final Integer CUSTOMER_ID = 1;
    private static final Integer RENTAL_ID   = 1;

    private static int paymentIdCounter = 99000;

    private Staff    staff1;
    private Staff    staff2;
    private Customer customer;
    private Rental   rental;

    @BeforeEach
    void setUp() {
        staff1   = staffRepository.findById(STAFF1_ID).orElseThrow();
        staff2   = staffRepository.findById(STAFF2_ID).orElseThrow();
        customer = customerRepository.findById(CUSTOMER_ID).orElseThrow();
        rental   = rentalRepository.findById(RENTAL_ID).orElseThrow();
    }

    private Payment createPayment(Staff staff, BigDecimal amount, LocalDateTime date) {
        Payment payment = new Payment();
        payment.setPaymentId(paymentIdCounter++);
        payment.setStaff(staff);
        payment.setCustomer(customer);
        payment.setRental(rental);
        payment.setAmount(amount);
        payment.setPaymentDate(date);

        Payment saved = paymentRepository.save(payment);
        paymentRepository.flush(); // Forces synchronization with database state
        return saved;
    }



    @Test
    @DisplayName("findTopByOrderByPaymentIdDesc - returns present when payments exist")
    void findTop_returnsPresent_whenPaymentsExist() {
        Optional<Payment> result = paymentRepository.findTopByOrderByPaymentIdDesc();
        assertThat(result).isPresent();
    }

    @Test
    @DisplayName("findTopByOrderByPaymentIdDesc - new payment becomes the top")
    void findTop_newPaymentBecomesTop() {
        Payment latest = createPayment(staff1, new BigDecimal("99.99"), LocalDateTime.now());

        Optional<Payment> result = paymentRepository.findTopByOrderByPaymentIdDesc();

        assertThat(result).isPresent();
        assertThat(result.get().getPaymentId()).isEqualTo(latest.getPaymentId());
    }

    @Test
    @DisplayName("findTopByOrderByPaymentIdDesc - returns highest paymentId among test payments")
    void findTop_returnsHighestPaymentId() {
        createPayment(staff1, new BigDecimal("5.00"),  LocalDateTime.now().minusDays(2));
        createPayment(staff1, new BigDecimal("15.00"), LocalDateTime.now().minusDays(1));
        Payment highest = createPayment(staff2, new BigDecimal("20.00"), LocalDateTime.now());

        Optional<Payment> result = paymentRepository.findTopByOrderByPaymentIdDesc();

        assertThat(result).isPresent();
        assertThat(result.get().getPaymentId()).isEqualTo(highest.getPaymentId());
    }


    @Test
    @DisplayName("findByStaff_StoreId - returns empty when storeId does not exist")
    void findByStoreId_returnsEmpty_whenStoreIdNotFound() {
        Page<Payment> result = paymentRepository.findByStaff_StoreId(
                9999, PageRequest.of(0, 10));

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByStaff_StoreId - all results belong to correct store")
    void findByStoreId_allResultsBelongToStore1() {
        Integer dynamicStoreId = staff1.getStore().getStoreId();

        createPayment(staff1, new BigDecimal("10.00"), LocalDateTime.now());

        Page<Payment> result = paymentRepository.findByStaff_StoreId(
                dynamicStoreId, PageRequest.of(0, 1000));

        assertThat(result.getContent())
                .extracting(p -> p.getStaff().getStore().getStoreId())
                .containsOnly(dynamicStoreId);
    }

    @Test
    @DisplayName("findByStaff_StoreId - respects page size")
    void findByStoreId_respectsPageSize() {
        Integer dynamicStoreId = staff1.getStore().getStoreId();

        for (int i = 1; i <= 5; i++) {
            createPayment(staff1, new BigDecimal(i * 10), LocalDateTime.now());
        }

        Page<Payment> result = paymentRepository.findByStaff_StoreId(
                dynamicStoreId, PageRequest.of(0, 2));

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isGreaterThanOrEqualTo(5);
    }

    @Test
    @DisplayName("findByStaff_StoreId - second page is accessible")
    void findByStoreId_secondPageAccessible() {
        Integer dynamicStoreId = staff1.getStore().getStoreId();

        for (int i = 1; i <= 4; i++) {
            createPayment(staff1, new BigDecimal(i * 5), LocalDateTime.now());
        }

        Page<Payment> result = paymentRepository.findByStaff_StoreId(
                dynamicStoreId, PageRequest.of(1, 2));

        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("findByStaff_StoreId - out of range page returns empty content")
    void findByStoreId_outOfRangePage_returnsEmpty() {
        Integer dynamicStoreId = staff1.getStore().getStoreId();

        Page<Payment> result = paymentRepository.findByStaff_StoreId(
                dynamicStoreId, PageRequest.of(10000, 10));

        assertThat(result.getContent()).isEmpty();
    }
}