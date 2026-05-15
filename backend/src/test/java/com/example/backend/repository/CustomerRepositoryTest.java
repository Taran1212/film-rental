package com.example.backend.repository;

import com.example.backend.entity.Address;
import com.example.backend.entity.Customer;
import com.example.backend.entity.Store;
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

    private Store store1;
    private Store store2;

    private Address address1;
    private Address address2;
    private Address address3;

    @BeforeEach
    void setUp() {

        // Stores
        store1 = new Store();
        store1.setStoreId(1);

        store2 = new Store();
        store2.setStoreId(2);

        // Addresses
        address1 = new Address();
        address1.setAddressId(101);

        address2 = new Address();
        address2.setAddressId(102);

        address3 = new Address();
        address3.setAddressId(103);

        // Customer 1
        customer1 = new Customer();
        customer1.setFirstName("John");
        customer1.setLastName("Doe");
        customer1.setEmail("john@example.com");
        customer1.setStoreId(store1);
        customer1.setAddress(address1);
        customer1.setActive(true);
        customer1.setCreateDate(LocalDateTime.now());
        customer1.setLastUpdate(LocalDateTime.now());

        // Customer 2
        customer2 = new Customer();
        customer2.setFirstName("Jane");
        customer2.setLastName("Smith");
        customer2.setEmail("jane@example.com");
        customer2.setStoreId(store1);
        customer2.setAddress(address2);
        customer2.setActive(true);
        customer2.setCreateDate(LocalDateTime.now());
        customer2.setLastUpdate(LocalDateTime.now());

        // Customer 3
        customer3 = new Customer();
        customer3.setFirstName("Johnny");
        customer3.setLastName("Walker");
        customer3.setEmail("johnny@example.com");
        customer3.setStoreId(store2);
        customer3.setAddress(address3);
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
                customerRepository.findByStoreId(store1, pageable);

        assertThat(customers).isNotNull();
        assertThat(customers.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Test countByStoreId")
    void testCountByStoreId() {

        Long count = customerRepository.countByStoreId(store1);

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Test search by first name")
    void testFindByFirstNameContaining() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Customer> customers =
                customerRepository
                        .findByStoreIdAndFirstNameContainingIgnoreCaseOrStoreIdAndLastNameContainingIgnoreCase(
                                store1, "john",
                                store1, "",
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
                                store1, "",
                                store1, "smith",
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
                                store1,
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

        var customer =
                customerRepository.findTopByOrderByCustomerIdDesc();

        assertThat(customer).isPresent();
    }

    @Test
    @DisplayName("Test no customer found for store")
    void testFindByStoreIdNoData() {

        Pageable pageable = PageRequest.of(0, 10);

        Store store99 = new Store();
        store99.setStoreId(99);

        Page<Customer> customers =
                customerRepository.findByStoreId(store99, pageable);

        assertThat(customers).isNotNull();
        assertThat(customers.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("Test countByStoreId when no customers exist")
    void testCountByStoreIdNoData() {

        Store store99 = new Store();
        store99.setStoreId(99);

        Long count = customerRepository.countByStoreId(store99);

        assertThat(count).isZero();
    }
}