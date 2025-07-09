package com.crossfit.pieds_croises.dto;

import com.crossfit.pieds_croises.model.PerformanceHistory;
import com.crossfit.pieds_croises.model.UserSubscription;
import com.crossfit.pieds_croises.model.WeightHistory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    private Long id;

    private String firstname;
    private String lastname;

    @Email(message = "Veuillez fournir une adresse email valide")
    private String email;

    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]{8,20}$", message = "Veuillez fournir un numéro de téléphone valide")
    private String phone;

    private String profilePicture;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Byte strikeCount;
    private List<UserSubscription> userSubscriptions;
    private List<WeightHistory> weightHistory;
    private List<PerformanceHistory> performanceHistoryList;

}

