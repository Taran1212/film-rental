package com.example.backend.repository;

import com.example.backend.entity.Address;
import com.example.backend.entity.Customer;
import com.example.backend.entity.Store;
import jakarta.transaction.Transactional;
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
@Transactional
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    private Store store1;
    private Store store2;

    private Address address1;
    private Address address2;
    private Address address3;

    @BeforeEach
    void setUp() {

        store1 = new Store();
        store1.setStoreId(1);

        store2 = new Store();
        store2.setStoreId(2);

        address1 = new Address();
        address1.setAddressId(101);

        address2 = new Address();
        address2.setAddressId(102);

        address3 = new Address();
        address3.setAddressId(103);

        Customer c1 = new Customer();
        c1.setFirstName("John");
        c1.setLastName("Doe");
        c1.setEmail("john@example.com");
        c1.setStore(store1);
        c1.setAddress(address1);
        c1.setActive(true);
        c1.setCreateDate(LocalDateTime.now());
        c1.setLastUpdate(LocalDateTime.now());

        Customer c2 = new Customer();
        c2.setFirstName("Jane");
        c2.setLastName("Smith");
        c2.setEmail("jane@example.com");
        c2.setStore(store1);
        c2.setAddress(address2);
        c2.setActive(true);
        c2.setCreateDate(LocalDateTime.now());
        c2.setLastUpdate(LocalDateTime.now());

        Customer c3 = new Customer();
        c3.setFirstName("Johnny");
        c3.setLastName("Walker");
        c3.setEmail("johnny@example.com");
        c3.setStore(store2);
        c3.setAddress(address3);
        c3.setActive(false);
        c3.setCreateDate(LocalDateTime.now());
        c3.setLastUpdate(LocalDateTime.now());

        customerRepository.save(c1);
        customerRepository.save(c2);
        customerRepository.save(c3);
    }

    @Test
    @DisplayName("Test findByStore")
    void testFindByStore() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Customer> customers =
                customerRepository.findByStore(store1, pageable);

        assertThat(customers.getTotalElements()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Test countByStore")
    void testCountByStore() {

        Long count = customerRepository.countByStore(store1);

        assertThat(count).isGreaterThan(0);
    }

    @Test
    @DisplayName("Test search by first name")
    void testFindByFirstNameContaining() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Customer> customers =
                customerRepository.findByStoreAndFirstNameContainingIgnoreCase(
                        store1,
                        "john",
                        pageable
                );
        assertThat(customers.getContent())
                .isNotEmpty();

        assertThat(customers.getContent())
                .allMatch(c ->
                        c.getFirstName().toLowerCase().contains("john")
                );    }

    @Test
    @DisplayName("Test search by last name")
    void testFindByLastNameContaining() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Customer> customers =
                customerRepository.findByStoreAndLastNameContainingIgnoreCase(
                        store1,
                        "smith",
                        pageable
                );

        assertThat(customers.getContent())
                .extracting(Customer::getLastName)
                .allMatch(name -> name.toLowerCase().contains("smith"));
    }

    @Test
    @DisplayName("Test findTopByOrderByCustomerIdDesc")
    void testFindTopByOrderByCustomerIdDesc() {

        var customer = customerRepository.findTopByOrderByCustomerIdDesc();

        assertThat(customer).isPresent();
    }

    @Test
    @DisplayName("Test no customer found for store")
    void testFindByStoreNoData() {

        Pageable pageable = PageRequest.of(0, 10);

        Store store99 = new Store();
        store99.setStoreId(99);

        Page<Customer> customers =
                customerRepository.findByStore(store99, pageable);

        assertThat(customers.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("Test countByStore when no customers exist")
    void testCountByStoreNoData() {

        Store store99 = new Store();
        store99.setStoreId(99);

        Long count = customerRepository.countByStore(store99);

        assertThat(count).isZero();
    }
}