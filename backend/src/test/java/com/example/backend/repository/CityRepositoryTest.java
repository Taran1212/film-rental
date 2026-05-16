package com.example.backend.repository;



import com.example.backend.entity.City;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class CityRepositoryTest {
    @Autowired
    private CityRepository cityRepo;

    @Test
    void testFindAllCitiesPagination() {
        Page<City> cities = cityRepo.findAll(PageRequest.of(0, 10));

        assertNotNull(cities);
        assertFalse(cities.isEmpty());
        assertEquals(10, cities.getContent().size());

        cities.forEach(city -> {
            System.out.println("City ID: " + city.getCityId());
            System.out.println("City: " + city.getCity());
            System.out.println("------------------------");
        });

    }

    @Test
    void testFindAllCitiesTotalElements() {
        Page<City> cities = cityRepo.findAll(PageRequest.of(0, 10));

        assertNotNull(cities);
        assertTrue(cities.getTotalElements() >= cities.getContent().size());
    }
}
