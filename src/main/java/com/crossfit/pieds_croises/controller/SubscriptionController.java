package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.SubscriptionCreateDto;
import com.crossfit.pieds_croises.dto.SubscriptionDto;
import com.crossfit.pieds_croises.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @GetMapping
    public ResponseEntity<List<SubscriptionDto>> getAllSubscriptions() {
        List<SubscriptionDto> subscriptionDtos = subscriptionService.getAllSubscriptions();
        return ResponseEntity.ok(subscriptionDtos);
    }

    @PostMapping
    public ResponseEntity<SubscriptionDto> addSubscription(@Valid @RequestBody SubscriptionCreateDto subscriptionCreateDto) {
        SubscriptionDto createdSubscription = subscriptionService.addSubscription(subscriptionCreateDto);
        return ResponseEntity.ok().body(createdSubscription);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionDto> getSubscriptionById(@PathVariable Long id) {
        SubscriptionDto subscriptionDto = subscriptionService.getSubscriptionById(id);
        if (subscriptionDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(subscriptionDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionDto> updateSubscription(@PathVariable Long id, @Valid @RequestBody SubscriptionDto subscriptionDto) {
        SubscriptionDto updatedSubscription = subscriptionService.updateSubscription(id, subscriptionDto);
        if (updatedSubscription == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedSubscription);
    }

}
