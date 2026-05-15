package com.example.backend.repository;

import com.example.backend.entity.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address,Integer> {

    Page<Address> findAll(Pageable pageable);

    Optional<Address> findTopByOrderByAddressIdDesc();






}
