package com.crossfit.pieds_croises.dto;

import com.crossfit.pieds_croises.enums.UserSubscriptionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor

public class UserSubscriptionDto {

    private Long id;

    //    @NotNull
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int freezeDaysRemaining;
    private UserSubscriptionStatus status;

    @NotNull(message = "User ID is required")
    private Long userId;
    @NotNull(message = "Subscription ID is required")
    private Long subscriptionId;

    private SubscriptionDto subscription;
}
