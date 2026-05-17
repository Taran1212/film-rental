package com.example.backend.repository;

import com.example.backend.dto.projection.CountryProjection;
import com.example.backend.entity.Country;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class CountryRepositoryTest {

    @Autowired
    private CountryRepository countryRepo;

    @Test
    void testFindAllCountriesPagination() {
        Page<Country> countries = countryRepo.findAll(PageRequest.of(0, 10));

        assertNotNull(countries);
        assertFalse(countries.isEmpty());
        assertEquals(10, countries.getContent().size());

        countries.forEach(country -> {
            System.out.println("Country ID: " + country.getCountryId());
            System.out.println("Country: " + country.getCountry());
            System.out.println("------------------------");
        });

    }

    @Test
    void testFindAllByOrderByCountryAsc() {

        List<CountryProjection> countries =
                countryRepo.findAllByOrderByCountryAsc();

        assertNotNull(countries);
        assertFalse(countries.isEmpty());

        countries.forEach(country -> {
            assertNotNull(country.getCountryId());
            assertNotNull(country.getName());
        });
    }
}
