package com.example.backend.controller.staff;

import com.example.backend.dto.StaffRegisterDto;
import com.example.backend.dto.projection.StaffDetailProjection;
import com.example.backend.dto.projection.StaffProjection;
import com.example.backend.service.staff.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STAFF')")
public class StaffController {

    private final StaffService staffService;

    @GetMapping
    public Page<StaffProjection> getMyStoreStaff(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return staffService.getMyStoreStaff(PageRequest.of(page, size));
    }

    @GetMapping("/search")
    public Page<StaffProjection> searchStaff(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return staffService.searchMyStoreStaff(name, PageRequest.of(page, size));
    }

    @PostMapping
    public String createStaff(@Valid @RequestBody StaffRegisterDto dto) {
        return staffService.createStaff(dto);
    }

    @GetMapping("/{id}")
    public StaffDetailProjection getStaffById(@PathVariable Integer id) {
        return staffService.getStaffById(id);
    }
}