package com.example.backend.repository;

import com.example.backend.entity.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City,Integer> {

    Page<City> findAll(Pageable pageable);

}
