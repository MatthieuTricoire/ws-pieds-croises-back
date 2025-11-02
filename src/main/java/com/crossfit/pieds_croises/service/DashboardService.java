package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.dto.DashboardStatsDTO;
import com.crossfit.pieds_croises.enums.UserSubscriptionStatus;
import com.crossfit.pieds_croises.model.UserCourse;
import com.crossfit.pieds_croises.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final CourseRepository courseRepository;
    private final UserCourseRepository userCourseRepository;

    public DashboardStatsDTO getDashboardStats() {
        return DashboardStatsDTO.builder()
                .totalUsers(getTotalUsers())
                .activeUsers(getActiveUsers())
                .monthlyRevenue(getMonthlyRevenue())
                .averageCourseOccupancyRate(getAverageCourseOccupancyRate())
                .build();
    }

    private Long getTotalUsers() {
        return userRepository.count();
    }

    private Long getActiveUsers() {
        return userSubscriptionRepository.countByStatus(UserSubscriptionStatus.ACTIVE);
    }

    private BigDecimal getMonthlyRevenue() {
        List<Integer> activeSubscriptionPrices = userSubscriptionRepository.findActiveSubscriptionsWithPrices();
        
        return activeSubscriptionPrices.stream()
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Double getAverageCourseOccupancyRate() {
        // Récupérer tous les cours des 30 derniers jours
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Object[]> courseOccupancyData = courseRepository.findCourseOccupancyData(thirtyDaysAgo);

        if (courseOccupancyData.isEmpty()) {
            return 0.0;
        }

        double totalOccupancyRate = courseOccupancyData.stream()
                .mapToDouble(result -> {
                    Long registeredCount = (Long) result[0];
                    Integer personLimit = (Integer) result[1];
                    return (double) registeredCount / personLimit * 100;
                })
                .sum();

        return totalOccupancyRate / courseOccupancyData.size();
    }
}