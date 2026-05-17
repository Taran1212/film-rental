package com.example.backend.service;

import com.example.backend.dto.StaffRegisterDto;
import com.example.backend.dto.projection.StaffDetailProjection;
import com.example.backend.entity.Staff;
import com.example.backend.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StaffService {

    private final StaffRepository staffRepository;

    public StaffDetailProjection getStaffById(Integer id, Integer storeId) {
        return staffRepository.findByStaffIdAndStoreId(id, storeId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
    }

    @Transactional
    public String createStaff(StaffRegisterDto dto) {
        if (staffRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        Integer nextStaffId = staffRepository
                .findTopByOrderByStaffIdDesc()
                .map(s -> s.getStaffId() + 1)
                .orElse(1);

        Staff staff = new Staff();
        staff.setStaffId(nextStaffId);
        staff.setFirstName(dto.getFirstName());
        staff.setLastName(dto.getLastName());
        staff.setUsername(dto.getUsername());
        staff.setEmail(dto.getEmail());
        staff.setPassword(dto.getPassword());
        staff.setStoreId(dto.getStoreId());
        staff.setActive(true);
        staff.setLastUpdate(LocalDateTime.now());
        staffRepository.save(staff);

        return "Staff created successfully";
    }
}