package com.example.backend.repository;

import com.example.backend.dto.projection.LanguageProjection;
import com.example.backend.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LanguageRepository extends JpaRepository<Language,Integer> {

    List<LanguageProjection> findAllByOrderByNameAsc();
}
