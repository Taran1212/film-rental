package com.example.backend.repository;

import com.example.backend.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer1;
    private Customer customer2;
    private Customer customer3;

    @BeforeEach
    void setUp() {

        customer1 = new Customer();
        customer1.setCustomerId(1);
        customer1.setStoreId(1);
        customer1.setFirstName("John");
        customer1.setLastName("Doe");
        customer1.setEmail("john@example.com");
        customer1.setAddressId(101);
        customer1.setActive(true);
        customer1.setCreateDate(LocalDateTime.now());
        customer1.setLastUpdate(LocalDateTime.now());

        customer2 = new Customer();
        customer2.setCustomerId(2);
        customer2.setStoreId(1);
        customer2.setFirstName("Jane");
        customer2.setLastName("Smith");
        customer2.setEmail("jane@example.com");
        customer2.setAddressId(102);
        customer2.setActive(true);
        customer2.setCreateDate(LocalDateTime.now());
        customer2.setLastUpdate(LocalDateTime.now());

        customer3 = new Customer();
        customer3.setCustomerId(3);
        customer3.setStoreId(2);
        customer3.setFirstName("Johnny");
        customer3.setLastName("Walker");
        customer3.setEmail("johnny@example.com");
        customer3.setAddressId(103);
        customer3.setActive(false);
        customer3.setCreateDate(LocalDateTime.now());
        customer3.setLastUpdate(LocalDateTime.now());

        customerRepository.save(customer1);
        customerRepository.save(customer2);
        customerRepository.save(customer3);
    }

    @Test
    @DisplayName("Test findByStoreId")
    void testFindByStoreId() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Customer> customers =
                customerRepository.findByStoreId(1, pageable);

        assertThat(customers).isNotNull();
        assertThat(customers.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Test countByStoreId")
    void testCountByStoreId() {

        Long count = customerRepository.countByStoreId(1);

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Test search by first name")
    void testFindByFirstNameContaining() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Customer> customers =
                customerRepository
                        .findByStoreIdAndFirstNameContainingIgnoreCaseOrStoreIdAndLastNameContainingIgnoreCase(
                                1, "john",
                                1, "",
                                pageable
                        );

        assertThat(customers).isNotNull();
        assertThat(customers.getTotalElements()).isEqualTo(1);
        assertThat(customers.getContent().get(0).getFirstName())
                .isEqualTo("John");
    }

    @Test
    @DisplayName("Test search by last name")
    void testFindByLastNameContaining() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Customer> customers =
                customerRepository
                        .findByStoreIdAndFirstNameContainingIgnoreCaseOrStoreIdAndLastNameContainingIgnoreCase(
                                1, "",
                                1, "smith",
                                pageable
                        );

        assertThat(customers).isNotNull();
        assertThat(customers.getTotalElements()).isEqualTo(1);
        assertThat(customers.getContent().get(0).getLastName())
                .isEqualTo("Smith");
    }

    @Test
    @DisplayName("Test full name search")
    void testFindByFullName() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Customer> customers =
                customerRepository
                        .findByStoreIdAndFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(
                                1,
                                "Jane",
                                "Smith",
                                pageable
                        );

        assertThat(customers).isNotNull();
        assertThat(customers.getTotalElements()).isEqualTo(1);

        Customer customer = customers.getContent().get(0);

        assertThat(customer.getFirstName()).isEqualTo("Jane");
        assertThat(customer.getLastName()).isEqualTo("Smith");
    }

    @Test
    @DisplayName("Test findTopByOrderByCustomerIdDesc")
    void testFindTopByOrderByCustomerIdDesc() {

        Customer customer =
                customerRepository.findTopByOrderByCustomerIdDesc();

        assertThat(customer).isNotNull();
        assertThat(customer.getCustomerId()).isEqualTo(3);
    }

    @Test
    @DisplayName("Test no customer found for store")
    void testFindByStoreIdNoData() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Customer> customers =
                customerRepository.findByStoreId(99, pageable);

        assertThat(customers).isNotNull();
        assertThat(customers.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("Test countByStoreId when no customers exist")
    void testCountByStoreIdNoData() {

        Long count = customerRepository.countByStoreId(99);

        assertThat(count).isEqualTo(0);
    }
}