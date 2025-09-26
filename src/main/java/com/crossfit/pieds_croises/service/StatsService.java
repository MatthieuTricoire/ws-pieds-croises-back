package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.dto.MonthlyStatsDto;
import com.crossfit.pieds_croises.dto.StatsDto;
import com.crossfit.pieds_croises.repository.UserSubscriptionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Service
@AllArgsConstructor
public class StatsService {
    private final UserSubscriptionRepository userSubscriptionRepository;

    public StatsDto getCurrentMonthStats() {
        LocalDateTime now = LocalDateTime.now();
        YearMonth currentMonth = YearMonth.now();
        YearMonth previousMonth = currentMonth.minusMonths(1);

        LocalDateTime currentMonthStart = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime currentMonthEnd = currentMonth.atEndOfMonth().atTime(23, 59, 59);

        Long currentActiveUsers = userSubscriptionRepository.countActiveUserSubscriptionsByMonth(
            currentMonthStart, currentMonthEnd
        );

        BigDecimal currentRevenue = userSubscriptionRepository.calculateMonthlyRevenue(
            currentMonthStart, currentMonthEnd
        );

        LocalDateTime previousMonthStart = previousMonth.atDay(1).atStartOfDay();
        LocalDateTime previousMonthEnd = previousMonth.atEndOfMonth().atTime(23, 59, 59);

        Long previousActiveUsers = userSubscriptionRepository.countActiveUserSubscriptionsByMonth(
            previousMonthStart, previousMonthEnd
        );

        Double progressionPercentage = calculatePercentage(previousActiveUsers, currentActiveUsers);

        return StatsDto.builder()
            .activeUsersCount(currentActiveUsers)
            .progressionPercentage(progressionPercentage)
            .monthlyRevenue(currentRevenue)
            .build();
    }

    public MonthlyStatsDto getMonthlyStats(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        Long activeUsers = userSubscriptionRepository.countActiveUserSubscriptionsByMonth(startDate, endDate);
        BigDecimal revenue = userSubscriptionRepository.calculateMonthlyRevenue(startDate, endDate);

        return MonthlyStatsDto.builder()
            .year(year)
            .month(month)
            .activeUsersCount(activeUsers)
            .revenue(revenue)
            .build();
    }

    private Double calculatePercentage(Long previousCount, Long currentCount) {
        if (previousCount == null || previousCount == 0) {
            return currentCount > 0 ? 100.0 : 0.0;
        }
        double difference = currentCount - previousCount;
        double percentage = (difference / previousCount) / 100;

        return BigDecimal.valueOf(percentage).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
