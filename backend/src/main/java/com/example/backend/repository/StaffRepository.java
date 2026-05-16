package com.example.backend.repository;

import com.example.backend.dto.projection.StaffProjection;
import com.example.backend.entity.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;
import com.example.backend.dto.projection.StaffDetailProjection;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {

    Optional<Staff> findByUsername(String username);

    Optional<Staff> findTopByOrderByStaffIdDesc();

    List<Staff> findByStoreId(Integer storeId);

    // ===== Projection-returning read methods (derived only) =====

    Page<StaffProjection> findProjectedByStoreId(Integer storeId, Pageable pageable);

    Page<StaffProjection>
    findProjectedByStoreIdAndFirstNameContainingIgnoreCaseOrStoreIdAndLastNameContainingIgnoreCaseOrStoreIdAndUsernameContainingIgnoreCase(
            Integer storeId1, String firstName,
            Integer storeId2, String lastName,
            Integer storeId3, String username,
            Pageable pageable);

    @EntityGraph(attributePaths = "address.city.country")
    Optional<StaffDetailProjection> findByStaffIdAndStoreId(Integer staffId, Integer storeId);
}