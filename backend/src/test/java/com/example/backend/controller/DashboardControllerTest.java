package com.example.backend.controller;

import com.example.backend.controller.dashboard.DashboardController;
import com.example.backend.dto.DashboardStatsDto;
import com.example.backend.dto.projection.RecentRentalProjection;
import com.example.backend.exception.GlobalExceptionHandler;
import com.example.backend.security.CustomStaffDetailsService;
import com.example.backend.security.JwtService;
import com.example.backend.service.dashboard.DashboardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DashboardController.class, excludeAutoConfiguration = {
        org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration.class,
        org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class DashboardControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private JwtService jwtService;
    @MockitoBean private CustomStaffDetailsService userDetailsService;
    @MockitoBean private DashboardService dashboardService;

    @Test
    @DisplayName("GET /api/dashboard/stats — DashboardStatsDto JSON shape")
    void shouldReturnStats() throws Exception {
        DashboardStatsDto stats = DashboardStatsDto.builder()
                .totalCustomers(329L)
                .totalMovies(1001L)
                .activeRentals(85L)
                .totalRevenue(33498.46)
                .build();
        when(dashboardService.getDashboardStats()).thenReturn(stats);

        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCustomers").value(329))
                .andExpect(jsonPath("$.totalMovies").value(1001))
                .andExpect(jsonPath("$.activeRentals").value(85))
                .andExpect(jsonPath("$.totalRevenue").value(33498.46));
    }

    @Test
    @DisplayName("GET /api/dashboard/recent-rentals — list of RecentRentalProjection")
    void shouldReturnRecentRentals() throws Exception {
        RecentRentalProjection p = new RecentRentalProjection() {
            public Integer getRentalId() { return 16053; }
            public String getMovieTitle() { return "ACADEMY DINOSAUR"; }
            public String getCustomerName() { return "Taran Hero"; }
            public LocalDateTime getRentalDate() { return LocalDateTime.of(2026, 5, 15, 4, 31); }
            public Boolean getReturned() { return true; }
        };
        when(dashboardService.getRecentRentals()).thenReturn(List.of(p));

        mockMvc.perform(get("/api/dashboard/recent-rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].rentalId").value(16053))
                .andExpect(jsonPath("$[0].movieTitle").value("ACADEMY DINOSAUR"))
                .andExpect(jsonPath("$[0].customerName").value("Taran Hero"))
                .andExpect(jsonPath("$[0].returned").value(true));
    }
}
