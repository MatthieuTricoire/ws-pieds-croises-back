package com.crossfit.pieds_croises.controller;

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
public class BoxSubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionDto> addSubscription(@Valid @RequestBody SubscriptionDto subscriptionDto) {
        SubscriptionDto createdSubscription = subscriptionService.addSubscription(subscriptionDto);
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

    @GetMapping("/box/{boxId}")
    public ResponseEntity<List<SubscriptionDto>> getSubscriptionsByBoxId(@PathVariable Long boxId) {
        List<SubscriptionDto> subscriptions = subscriptionService.getAllSubscriptionsByBoxId(boxId);
        if (subscriptions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(subscriptions);
    }
}
