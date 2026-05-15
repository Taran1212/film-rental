package com.example.backend.repository;

import com.example.backend.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language,Integer> {
}
