package com.example.backend.repository;


import com.example.backend.entity.Store;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class StoreRepositoryTest {

    @Autowired
    private StoreRepository storeRepo;

    @Test
    void testFindTopByOrderByStoreIdDesc() {

        var latestStore = storeRepo.findTopByOrderByStoreIdDesc();

        assertTrue(latestStore.isPresent());

        System.out.println("Latest Store ID: " + latestStore.get().getStoreId());
        System.out.println("Manager Staff ID: " + latestStore.get().getManagerStaffId());
        System.out.println("Address ID: " + latestStore.get().getAddressId());
        System.out.println("Last Update: " + latestStore.get().getLastUpdate());

        assertNotNull(latestStore.get().getStoreId());
    }

    @Test
    void testFindTopByOrderByStoreIdDescReturnsHighestId() {

        var latestStore = storeRepo.findTopByOrderByStoreIdDesc();

        assertTrue(latestStore.isPresent());

        Integer highestId = storeRepo.findAll()
                .stream()
                .map(Store::getStoreId)
                .max(Integer::compareTo)
                .orElse(null);

        assertEquals(highestId, latestStore.get().getStoreId());
    }

    @Test
    void testFindStoreById() {
        Store store = storeRepo.findById(1).orElse(null);
        assertNotNull(store);
        assertEquals(1, store.getStoreId());
    }

    @Test
    void testFindStoreByIdNotFound() {
        Store store = storeRepo.findById(9999).orElse(null);
        assertNull(store);
    }
}