package com.example.backend.repository;

import com.example.backend.dto.projection.CityProjection;
import com.example.backend.entity.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<City,Integer> {

    Page<City> findAll(Pageable pageable);

    List<CityProjection> findByCountryIdOrderByCityAsc(Integer countryId);
}
