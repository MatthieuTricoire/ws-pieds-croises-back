package com.crossfit.pieds_croises.repository;

import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.model.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    List<UserSubscription> findByUser(User user);

    Optional<UserSubscription> findTopByUserAndEndDateAfterOrderByEndDateDesc(User user, LocalDateTime endDate);

    Optional<UserSubscription> findByUserAndStartDateBeforeAndEndDateAfter(User user, LocalDateTime now, LocalDateTime now2);
}
