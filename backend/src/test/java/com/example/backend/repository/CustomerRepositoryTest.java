package com.example.backend.repository;


import com.example.backend.dto.projection.CustomerProjection;
import com.example.backend.entity.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @DisplayName("Sakila customer #1 is MARY SMITH at store 1")
    void shouldFindCustomerEntityById() {
        Optional<Customer> cust = customerRepository.findById(1);

        assertThat(cust).isPresent();
        assertThat(cust.get().getFirstName()).isEqualTo("MARY");
        assertThat(cust.get().getLastName()).isEqualTo("SMITH");
        assertThat(cust.get().getStore().getStoreId()).isEqualTo(1);
    }

    @Test
    @DisplayName("findProjectedByCustomerId — SpEL fullName concat works")
    void shouldFetchSingleProjection() {
        Optional<CustomerProjection> proj = customerRepository.findProjectedByCustomerId(1);

        assertThat(proj).isPresent();
        assertThat(proj.get().getCustomerId()).isEqualTo(1);
        assertThat(proj.get().getFullName()).isEqualToIgnoringCase("MARY SMITH");
        assertThat(proj.get().getEmail()).contains("@");
        assertThat(proj.get().getActive()).isNotNull();
    }

    @Test
    @DisplayName("Empty Optional for unknown customer id")
    void shouldReturnEmptyForUnknownId() {
        assertThat(customerRepository.findProjectedByCustomerId(999_999)).isEmpty();
    }

    @Test
    @DisplayName("findProjectedByStoreId — paginated and scoped to that store only")
    void shouldFindCustomersByStoreWithPagination() {
        Page<CustomerProjection> page = customerRepository
                .findProjectedByStore_StoreId(1, PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getContent()).allSatisfy(c ->
                assertThat(c.getStoreId()).isEqualTo(1));
        assertThat(page.getTotalElements()).isPositive();
    }

    @Test
    @DisplayName("OR-search by first or last name within store")
    void shouldSearchByFirstOrLastName() {
        Page<CustomerProjection> page = customerRepository
                .findProjectedByStore_StoreIdAndFirstNameContainingIgnoreCaseOrStore_StoreIdAndLastNameContainingIgnoreCase(
                        1, "mary", 1, "smith", PageRequest.of(0, 10));

        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent()).anySatisfy(c ->
                assertThat(c.getFullName()).containsIgnoringCase("MARY"));
    }

    @Test
    @DisplayName("AND-search by full name within store")
    void shouldSearchByFullName() {
        Page<CustomerProjection> page = customerRepository
                .findProjectedByStore_StoreIdAndFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(
                        1, "mary", "smith", PageRequest.of(0, 10));

        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent()).anySatisfy(c ->
                assertThat(c.getFullName()).containsIgnoringCase("MARY SMITH"));
    }

    @Test
    @DisplayName("countByStoreId — store 1 has many customers")
    void shouldCountCustomersInStore() {
        Long count = customerRepository.countByStore_StoreId(1);
        assertThat(count).isNotNull().isPositive();
    }

    @Test
    @DisplayName("findTopByOrderByCustomerIdDesc — highest customer id wins")
    void shouldReturnLatestCustomer() {
        Customer latest = customerRepository.findTopByOrderByCustomerIdDesc();
        assertThat(latest).isNotNull();
        assertThat(latest.getCustomerId()).isPositive();
    }

    @Test
    @DisplayName("Customer → Store + Address relations are navigable")
    void shouldNavigateRelations() {
        Customer cust = customerRepository.findById(1).orElseThrow();
        assertThat(cust.getStore()).isNotNull();
        assertThat(cust.getStore().getStoreId()).isEqualTo(1);
        assertThat(cust.getAddress()).isNotNull();
        assertThat(cust.getAddress().getAddressId()).isNotNull();
    }
}
