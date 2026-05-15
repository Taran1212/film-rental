package com.example.backend.repository;

import com.example.backend.entity.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country,Integer> {

    Page<Country> findAll(Pageable pageable);

}
