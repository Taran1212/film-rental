package com.example.backend.repository;

import com.example.backend.dto.projection.CategoryProjection;
import com.example.backend.entity.Category;
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
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Sakila category #1 is Action")
    void shouldFindActionCategory() {
        Optional<Category> cat = categoryRepository.findById(1);
        assertThat(cat).isPresent();
        assertThat(cat.get().getName()).isEqualTo("Action");
    }

    @Test
    @DisplayName("findAllByOrderByNameAsc — projection, ~16 categories alphabetically")
    void shouldListProjectionsAlphabetically() {
        List<CategoryProjection> cats = categoryRepository.findAllByOrderByNameAsc();

        assertThat(cats).isNotEmpty();
        assertThat(cats.size()).isGreaterThanOrEqualTo(16);
        assertThat(cats).allSatisfy(c -> {
            assertThat(c.getCategoryId()).isNotNull();
            assertThat(c.getName()).isNotBlank();
        });
        // Sorted
        for (int i = 1; i < cats.size(); i++) {
            assertThat(cats.get(i - 1).getName().compareToIgnoreCase(cats.get(i).getName()))
                    .isLessThanOrEqualTo(0);
        }
    }
}
