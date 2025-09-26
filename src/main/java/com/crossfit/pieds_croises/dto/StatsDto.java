package com.crossfit.pieds_croises.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class StatsDto {
    private Long activeUsersCount;
    private Double progressionPercentage;
    private BigDecimal monthlyRevenue;
}
