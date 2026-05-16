package com.example.backend.repository;

import com.example.backend.dto.projection.StaffDetailProjection;
import com.example.backend.dto.projection.StaffProjection;
import com.example.backend.entity.Staff;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StaffRepositoryTest {

    @Autowired
    private StaffRepository staffRepository;

    @Test
    @DisplayName("Sakila staff #1 is Mike Hillyer at store 1")
    void shouldFindStaffById() {
        Optional<Staff> staff = staffRepository.findById(1);

        assertThat(staff).isPresent();
        assertThat(staff.get().getFirstName()).isEqualTo("Mike");
        assertThat(staff.get().getLastName()).isEqualTo("Hillyer");
        assertThat(staff.get().getStoreId()).isEqualTo(1);
    }

    @Test
    @DisplayName("findByUsername — username Mike resolves to staff_id 1")
    void shouldFindStaffByUsername() {
        Optional<Staff> staff = staffRepository.findByUsername("Mike");

        assertThat(staff).isPresent();
        assertThat(staff.get().getStaffId()).isEqualTo(1);
    }

    @Test
    @DisplayName("findByUsername — unknown username returns empty")
    void shouldReturnEmptyForUnknownUsername() {
        assertThat(staffRepository.findByUsername("NoSuchUser_ZZZ")).isEmpty();
    }

    @Test
    @DisplayName("findByStoreId — entity list scoped to a store")
    void shouldListEntitiesByStore() {
        List<Staff> staff = staffRepository.findByStoreId(1);

        assertThat(staff).isNotEmpty();
        assertThat(staff).allSatisfy(s -> assertThat(s.getStoreId()).isEqualTo(1));
    }

    @Test
    @DisplayName("findProjectedByStoreId — projection with fullName SpEL concat")
    void shouldListProjectionsByStore() {
        Page<StaffProjection> page = staffRepository
                .findProjectedByStoreId(1, PageRequest.of(0, 10));

        assertThat(page.getContent()).isNotEmpty();
        StaffProjection first = page.getContent().get(0);
        assertThat(first.getStaffId()).isNotNull();
        assertThat(first.getFullName()).isNotBlank().contains(" "); // first + " " + last
        assertThat(first.getEmail()).isNotBlank();
    }

    @Test
    @DisplayName("Projection search by name or username, scoped to store")
    void shouldSearchProjectionsByName() {
        Page<StaffProjection> page = staffRepository
                .findProjectedByStoreIdAndFirstNameContainingIgnoreCaseOrStoreIdAndLastNameContainingIgnoreCaseOrStoreIdAndUsernameContainingIgnoreCase(
                        1, "Mike", 1, "Mike", 1, "Mike", PageRequest.of(0, 10));

        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent()).anySatisfy(s ->
                assertThat(s.getFullName()).containsIgnoringCase("Mike"));
    }

    @Test
    @DisplayName("findByStaffIdAndStoreId — detail projection joins address → city → country")
    void shouldFetchDetailWithAddressJoins() {
        Optional<StaffDetailProjection> detail =
                staffRepository.findByStaffIdAndStoreId(1, 1);

        assertThat(detail).isPresent();
        StaffDetailProjection d = detail.get();
        assertThat(d.getStaffId()).isEqualTo(1);
        assertThat(d.getFullName()).containsIgnoringCase("Mike Hillyer");
        assertThat(d.getUsername()).isEqualTo("Mike");
        assertThat(d.getStoreId()).isEqualTo(1);
        // Joined fields should be present
        assertThat(d.getAddress()).isNotBlank();
        assertThat(d.getCity()).isNotBlank();
        assertThat(d.getCountry()).isNotBlank();
    }

    @Test
    @DisplayName("Detail projection — staff not in this store returns empty")
    void detailEnforcesStoreScope() {
        // staff 1 belongs to store 1; querying with store 2 must return empty
        assertThat(staffRepository.findByStaffIdAndStoreId(1, 2)).isEmpty();
    }

    @Test
    @DisplayName("Staff → Address + Store relations are navigable")
    void shouldNavigateRelations() {
        Staff staff = staffRepository.findById(1).orElseThrow();

        assertThat(staff.getStore()).isNotNull();
        assertThat(staff.getStore().getStoreId()).isEqualTo(1);
        assertThat(staff.getAddress()).isNotNull();
        assertThat(staff.getAddress().getCity()).isNotNull();
        assertThat(staff.getAddress().getCity().getCountry()).isNotNull();
    }

    @Test
    @DisplayName("findTopByOrderByStaffIdDesc — used by createStaff to generate next id")
    void shouldReturnTopStaffId() {
        Optional<Staff> top = staffRepository.findTopByOrderByStaffIdDesc();
        assertThat(top).isPresent();
        assertThat(top.get().getStaffId()).isPositive();
    }
}
