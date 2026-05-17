package com.example.backend.controller.dashboard;

import com.example.backend.dto.DashboardStatsDto;
import com.example.backend.dto.projection.RecentRentalProjection;
import com.example.backend.service.dashboard.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@PreAuthorize("hasRole('STAFF')")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public DashboardStatsDto getStats() {
        return dashboardService.getDashboardStats();
    }

    @GetMapping("/recent-rentals")
    public List<RecentRentalProjection> getRecentRentals() {
        return dashboardService.getRecentRentals();
    }
}