package com.crossfit.pieds_croises.repository;

import com.crossfit.pieds_croises.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}
