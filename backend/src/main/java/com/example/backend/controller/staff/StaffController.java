package com.example.backend.controller.staff;

import com.example.backend.dto.StaffRegisterDto;
import com.example.backend.dto.projection.StaffDetailProjection;
import com.example.backend.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @PostMapping
    public String createStaff(@Valid @RequestBody StaffRegisterDto dto) {
        return staffService.createStaff(dto);
    }

    @GetMapping("/{id}")
    public StaffDetailProjection getStaffById(
            @PathVariable Integer id,
            @RequestParam Integer storeId) {
        return staffService.getStaffById(id, storeId);
    }
}