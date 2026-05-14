package com.example.backend.repository;


import com.example.backend.entity.Store;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StoreRepositoryTest {

    @Autowired
    private StoreRepository storeRepo;

    @Test
    void testFindTopByOrderByStoreIdDesc() {

        Store latestStore = storeRepo.findTopByOrderByStoreIdDesc();

        assertNotNull(latestStore);

        System.out.println("Latest Store ID: " + latestStore.getStoreId());
        System.out.println("Manager Staff ID: " + latestStore.getManagerStaffId());
        System.out.println("Address ID: " + latestStore.getAddressId());
        System.out.println("Last Update: " + latestStore.getLastUpdate());

        assertNotNull(latestStore.getStoreId());
    }

    @Test
    void testFindTopByOrderByStoreIdDescReturnsHighestId() {

        Store latestStore = storeRepo.findTopByOrderByStoreIdDesc();

        assertNotNull(latestStore);

        Integer highestId = storeRepo.findAll()
                .stream()
                .map(Store::getStoreId)
                .max(Integer::compareTo)
                .orElse(null);

        assertEquals(highestId, latestStore.getStoreId());
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