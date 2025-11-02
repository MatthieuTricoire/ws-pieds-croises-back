package com.crossfit.pieds_croises.repository;

import com.crossfit.pieds_croises.enums.UserSubscriptionStatus;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.model.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    List<UserSubscription> findByUser(User user);

    Optional<UserSubscription> findByUserAndStatus(User user, UserSubscriptionStatus status);

    Optional<UserSubscription> findTopByUserAndEndDateAfterOrderByEndDateDesc(User user, LocalDateTime endDate);

    Optional<UserSubscription> findByUserAndStartDateBeforeAndEndDateAfter(User user, LocalDateTime now,
                                                                           LocalDateTime now2);

    @Query("SELECT COUNT(DISTINCT us.user) from UserSubscription us " +
        "WHERE us.status = 'ACTIVE' " +
        "AND us.startDate<= :currentDate " +
        "AND us.endDate >= :currentDate")
    Long countActiveUserSubscriptionsForDate(@Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT COUNT(DISTINCT us.user) FROM UserSubscription us " +
        "WHERE us.status = 'ACTIVE' " +
        "AND us.startDate <= :endDate " +
        "AND us.endDate >= :startDate")
    Long countActiveUserSubscriptionsByMonth(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(s.price), 0) FROM UserSubscription us " +
        "JOIN us.subscription s " +
        "WHERE us.status = 'ACTIVE' " +
        "AND us.startDate <= :endDate " +
        "AND us.endDate >= :startDate")
    BigDecimal calculateMonthlyRevenue(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    Long countByStatus(UserSubscriptionStatus status);

    @Query("SELECT s.price FROM UserSubscription us JOIN us.subscription s WHERE us.status = 'ACTIVE'")
    List<Integer> findActiveSubscriptionsWithPrices();
}
