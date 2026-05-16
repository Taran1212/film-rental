package com.example.backend.repository;

import com.example.backend.dto.projection.CountryProjection;
import com.example.backend.entity.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country,Integer> {

    Page<Country> findAll(Pageable pageable);

    List<CountryProjection> findAllByOrderByCountryAsc();

}
