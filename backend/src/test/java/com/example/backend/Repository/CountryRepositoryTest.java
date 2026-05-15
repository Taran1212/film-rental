package com.example.backend.repository;

import com.example.backend.entity.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
    void testFindAllCountriesTotalElements() {
        Page<Country> countries = countryRepo.findAll(PageRequest.of(0, 10));

        assertNotNull(countries);

        assertTrue(
                countries.getTotalElements() >= countries.getContent().size()
        );
    }
}
