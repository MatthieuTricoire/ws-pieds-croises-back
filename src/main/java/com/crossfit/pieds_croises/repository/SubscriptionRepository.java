package com.crossfit.pieds_croises.repository;

import com.crossfit.pieds_croises.model.Box;
import com.crossfit.pieds_croises.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    boolean existsByName(String name);

    boolean existsByPrice(Integer price);

    List<Subscription> findByBox(Box box);
}
