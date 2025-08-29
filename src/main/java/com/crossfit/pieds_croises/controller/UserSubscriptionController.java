package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.UserSubscriptionDto;
import com.crossfit.pieds_croises.enums.UserSubscriptionStatus;
import com.crossfit.pieds_croises.service.UserSubscriptionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/user-subscriptions")
public class UserSubscriptionController {

    private final UserSubscriptionService userSubscriptionService;

    @PostAuthorize("hasRole('ADMIN') or returnObject.body.userId == authentication.principal.id")
    @PostMapping()
    public ResponseEntity<UserSubscriptionDto> createUserSubscription(
        @Valid @RequestBody UserSubscriptionDto userSubscriptionDto) {
        UserSubscriptionDto createdUserSubscription = userSubscriptionService.createUserSubscription(userSubscriptionDto);
        return ResponseEntity.ok(createdUserSubscription);
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserSubscriptions(@PathVariable Long userId, @RequestParam(required = false) UserSubscriptionStatus status) {
        if (status.equals(UserSubscriptionStatus.ACTIVE)) {
            UserSubscriptionDto activeUserSubscription = userSubscriptionService.getActiveUserSubscription(userId);
            return ResponseEntity.ok(activeUserSubscription);
        }

        List<UserSubscriptionDto> subs = userSubscriptionService.getAllUserSubscriptionsByUserId(userId);
        return ResponseEntity.ok(subs);
    }

    @PostAuthorize("hasRole('ADMIN') or returnObject.body.userId == authentication.principal.id")
    @GetMapping("/{userSubscriptionId}")
    public ResponseEntity<UserSubscriptionDto> getUserSubscriptionById(@PathVariable Long userSubscriptionId) {
        UserSubscriptionDto userSubscriptionDto = userSubscriptionService.getUserSubscriptionById(userSubscriptionId);
        return ResponseEntity.ok(userSubscriptionDto);
    }

    @PreAuthorize("hasRole('ADMIN') or @userSubscriptionService.isOwnerOfSubscription(#userSubscriptionId)")
    @PutMapping("/{userSubscriptionId}/freeze")
    public ResponseEntity<Void> freezeUserSubscription(@PathVariable Long userSubscriptionId,
                                                       @RequestBody Map<String, LocalDateTime> freezeDates) {
        LocalDateTime freezeStartDate = freezeDates.get("freezeStartDate");
        LocalDateTime freezeEndDate = freezeDates.get("freezeEndDate");
        userSubscriptionService.freezeUserSubscription(userSubscriptionId, freezeStartDate, freezeEndDate);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN') or @userSubscriptionService.isOwnerOfSubscription(#userSubscriptionId)")
    @PutMapping("/{userSubscriptionId}/cancel")
    public ResponseEntity<Void> cancelUserSubscription(@PathVariable Long userSubscriptionId) {
        userSubscriptionService.cancelUserSubscription(userSubscriptionId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userSubscriptionId}")
    public ResponseEntity<Void> deleteUserSubscription(@PathVariable Long userSubscriptionId) {
        userSubscriptionService.deleteUserSubscription(userSubscriptionId);
        return ResponseEntity.noContent().build();
    }
}
