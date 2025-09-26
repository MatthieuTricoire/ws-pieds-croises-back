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
public class MonthlyStatsDto {
    private int year;
    private int month;
    private Long activeUsersCount;
    private BigDecimal revenue;
}
