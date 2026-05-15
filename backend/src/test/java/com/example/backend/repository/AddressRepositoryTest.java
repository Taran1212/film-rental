package com.example.backend.repository;


import com.example.backend.entity.Address;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AddressRepositoryTest {
    @Autowired
    private AddressRepository addressRepo;

    @Test
    void testFindAllAddressesPagination() {
        Page<Address> addresses = addressRepo.findAll(PageRequest.of(0, 10));

        assertNotNull(addresses);
        assertFalse(addresses.isEmpty());
        assertEquals(10, addresses.getContent().size());

        addresses.forEach(address -> {
            System.out.println("Address ID: " + address.getAddressId());
            System.out.println("Address: " + address.getAddress());
            System.out.println("------------------------");
        });
    }

    @Test
    void testFindTopByOrderByAddressIdDesc() {
        Optional<Address> latestAddress = addressRepo.findTopByOrderByAddressIdDesc();

        assertNotNull(latestAddress);
        assertTrue(latestAddress.isPresent());

        Address address = latestAddress.get();

        System.out.println("Latest Address ID: " + address.getAddressId());
        System.out.println("Latest Address: " + address.getAddress());

        assertNotNull(address.getAddressId());
        assertNotNull(address.getAddress());
    }

    @Test
    void testLatestAddressHasHighestAddressId() {
        Optional<Address> latestAddress = addressRepo.findTopByOrderByAddressIdDesc();

        assertTrue(latestAddress.isPresent());

        Integer latestAddressId = latestAddress.get().getAddressId();

        Page<Address> addresses = addressRepo.findAll(PageRequest.of(0, 1000));

        assertTrue(
                addresses.getContent().stream()
                        .allMatch(address -> latestAddressId >= address.getAddressId())
        );
    }
}
