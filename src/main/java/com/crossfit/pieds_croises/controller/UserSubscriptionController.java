package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.UserSubscriptionDto;
import com.crossfit.pieds_croises.service.UserSubscriptionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/user-subscriptions")
public class UserSubscriptionController {

    private final UserSubscriptionService userSubscriptionService;

    @PostMapping()
    public ResponseEntity<UserSubscriptionDto> createUserSubscription(
            @Valid @RequestBody UserSubscriptionDto userSubscriptionDto) {
        UserSubscriptionDto createdUserSubscription = userSubscriptionService.createUserSubscription(userSubscriptionDto);
        return ResponseEntity.ok(createdUserSubscription);
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserSubscriptionDto>> getUserSubscription(@PathVariable Long userId) {
        List<UserSubscriptionDto> userSubscriptionDto = userSubscriptionService.getAllUserSubscriptionsByUserId(userId);
        return ResponseEntity.ok(userSubscriptionDto);
    }

    @GetMapping("/{userSubscriptionId}")
    public ResponseEntity<UserSubscriptionDto> getUserSubscriptionById(@PathVariable Long userSubscriptionId) {
        UserSubscriptionDto userSubscriptionDto = userSubscriptionService.getUserSubscriptionById(userSubscriptionId);
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
