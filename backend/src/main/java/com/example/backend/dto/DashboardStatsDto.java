package com.example.backend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DashboardStatsDto {

    private Long totalCustomers;

    private Long totalMovies;

    private Long activeRentals;

    private Double totalRevenue;
}