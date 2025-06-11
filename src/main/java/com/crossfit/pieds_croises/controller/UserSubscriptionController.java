package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.UserSubscriptionDto;
import com.crossfit.pieds_croises.service.UserSubscriptionService;
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

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/user-subscriptions")
public class UserSubscriptionController {

    private final UserSubscriptionService userSubscriptionService;

    @PostMapping()
    public ResponseEntity<UserSubscriptionDto> createUserSubscription(
        @RequestBody UserSubscriptionDto userSubscriptionDto) {
        UserSubscriptionDto createdUserSubscription = userSubscriptionService.createUserSubscription(userSubscriptionDto);
        return ResponseEntity.ok(createdUserSubscription);
    }

    @PutMapping("/{userSubscriptionId}")
    public ResponseEntity<UserSubscriptionDto> updateUserSubscription(
        @PathVariable Long userSubscriptionId, @RequestBody UserSubscriptionDto userSubscriptionDto) {
        UserSubscriptionDto updatedUserSubscription = userSubscriptionService.updateUserSubscription(userSubscriptionId, userSubscriptionDto);
        return ResponseEntity.ok(updatedUserSubscription);
    }

    @GetMapping("/{userSubscriptionId}")
    public ResponseEntity<UserSubscriptionDto> getUserSubscription(@PathVariable Long userSubscriptionId) {
        UserSubscriptionDto userSubscriptionDto = userSubscriptionService.getUserSubscription(userSubscriptionId);
        return ResponseEntity.ok(userSubscriptionDto);
    }

    @PutMapping("/{userSubscriptionId}/freeze")
    public ResponseEntity<Void> freezeUserSubscription(@PathVariable Long userSubscriptionId, @RequestBody Map<String, LocalDateTime> freezeDates) {
        LocalDateTime freezeStartDate = freezeDates.get("freezeStartDate");
        LocalDateTime freezeEndDate = freezeDates.get("freezeEndDate");
        userSubscriptionService.freezeUserSubscription(userSubscriptionId, freezeStartDate, freezeEndDate);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userSubscriptionId}")
    public ResponseEntity<Void> deleteUserSubscription(@PathVariable Long userSubscriptionId) {
        userSubscriptionService.deleteUserSubscription(userSubscriptionId);
        return ResponseEntity.noContent().build();
    }
}
