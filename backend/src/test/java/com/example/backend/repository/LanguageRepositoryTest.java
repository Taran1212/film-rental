package com.example.backend.repository;

import com.example.backend.dto.projection.LanguageProjection;
import com.example.backend.entity.Language;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LanguageRepositoryTest {

    @Autowired
    private LanguageRepository languageRepository;

    @Test
    @DisplayName("Sakila language #1 is English")
    void shouldFindEnglishById() {
        Optional<Language> lang = languageRepository.findById(1);
        assertThat(lang).isPresent();
        assertThat(lang.get().getName().trim()).isEqualTo("English");
    }

    @Test
    @DisplayName("findAllByOrderByNameAsc — projection list, alphabetical")
    void shouldListProjections() {
        List<LanguageProjection> langs = languageRepository.findAllByOrderByNameAsc();

        assertThat(langs).isNotEmpty();
        assertThat(langs).allSatisfy(l -> {
            assertThat(l.getLanguageId()).isNotNull();
            assertThat(l.getName()).isNotBlank();
        });
    }
}
