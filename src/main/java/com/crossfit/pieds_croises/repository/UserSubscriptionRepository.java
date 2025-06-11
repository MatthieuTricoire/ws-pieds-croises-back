package com.crossfit.pieds_croises.repository;

import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.model.UserSubscription;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
  Optional<UserSubscription> findByUser(User user);

}
