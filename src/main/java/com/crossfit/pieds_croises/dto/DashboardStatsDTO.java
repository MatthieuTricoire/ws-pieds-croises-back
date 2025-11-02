package com.crossfit.pieds_croises.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {

    private Long totalUsers;
    private Long activeUsers;
    private BigDecimal monthlyRevenue;
    private Double averageCourseOccupancyRate;

}