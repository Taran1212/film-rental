package com.example.backend.repository;



import com.example.backend.dto.projection.CityProjection;
import com.example.backend.entity.City;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

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
    void testFindByCountryCountryIdOrderByCityAsc() {

        List<CityProjection> cities =
                cityRepo.findByCountry_CountryIdOrderByCityAsc(44);

        assertNotNull(cities);
        assertFalse(cities.isEmpty());

        cities.forEach(city -> {
            assertNotNull(city.getCityId());
            assertNotNull(city.getName());
            assertEquals(44, city.getCountryId());
        });
    }

    @Test
    void testFindTopByOrderByCityIdDesc() {

        Optional<City> latestCity =
                cityRepo.findTopByOrderByCityIdDesc();

        assertTrue(latestCity.isPresent());

        assertNotNull(latestCity.get().getCityId());
        assertNotNull(latestCity.get().getCity());
    }
}
