package com.example.backend.repository;

import com.example.backend.entity.Staff;
import jakarta.transaction.Transactional;
import org.hibernate.annotations.TargetEmbeddable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class StaffRepositoryTest {


    @Autowired
    private StaffRepository staffRepository;

    @Test
    void shouldReturnStaffWhenUsernameExists() {
        Optional<Staff> result = staffRepository.findByUsername("Mike");
        assertTrue(result.isPresent());
        assertEquals("Mike", result.get().getUsername());
    }

    @Test
    void shouldReturnEmptyWhenUsernameDoesNotExist() {
        Optional<Staff> result = staffRepository.findByUsername("RandomXYZ");
        assertFalse(result.isPresent());
    }

    @Test

    void shouldReturnLastInsertedStaff() {
        Optional<Staff> result = staffRepository.findTopByOrderByStaffIdDesc();
        assertTrue(result.isPresent());
        assertNotNull(result.get().getStaffId());
    }

    @Test
    void shouldReturnStaffListForStore1() {
        List<Staff> result = staffRepository.findByStoreId(1);
        assertFalse(result.isEmpty());
        assertEquals(1, result.get(0).getStoreId());
    }

    @Test
    void shouldReturnStaffListForStore2() {
        List<Staff> result = staffRepository.findByStoreId(2);
        assertFalse(result.isEmpty());
        assertEquals(2, result.get(0).getStoreId());
    }

    @Test
    void shouldReturnEmptyListForNonExistentStore() {
        List<Staff> result = staffRepository.findByStoreId(999);
        assertTrue(result.isEmpty());
    }


    @Test
    void shouldReturnStaffWhenIdExists() {
        Optional<Staff> result = staffRepository.findById(1);
        assertTrue(result.isPresent());
        assertEquals("Mike", result.get().getFirstName());
    }

    @Test
    void shouldReturnEmptyWhenIdDoesNotExist() {
        Optional<Staff> result = staffRepository.findById(999);
        assertFalse(result.isPresent());
    }

    @Test
    void shouldSaveStaffSuccessfully() {
        Staff newStaff = new Staff();
        newStaff.setStaffId(99);
        newStaff.setFirstName("Test");
        newStaff.setLastName("User");
        newStaff.setEmail("test.user@sakila.com");
        newStaff.setStoreId(1);
        newStaff.setAddressId(3);
        newStaff.setActive(true);
        newStaff.setUsername("testuser");
        newStaff.setPassword("testpass");

        Staff saved = staffRepository.save(newStaff);

        assertNotNull(saved.getStaffId());
        assertEquals("Test", saved.getFirstName());
    }

    @Test
    void shouldReturnTrueWhenStaffExists() {
        boolean exists = staffRepository.existsById(1);
        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenStaffDoesNotExist() {
        boolean exists = staffRepository.existsById(999);
        assertFalse(exists);
    }
}