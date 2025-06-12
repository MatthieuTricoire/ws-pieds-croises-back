package com.crossfit.pieds_croises.dto;

import com.crossfit.pieds_croises.model.PerformanceHistory;
import com.crossfit.pieds_croises.model.UserSubscription;
import com.crossfit.pieds_croises.model.WeightHistory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "First name is required")
    private String firstname;

    @NotBlank(message = "Last name is required")
    private String lastname;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    @NotBlank(message = """
            Phone number is required""")
    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]{8,20}$", message = "Please provide a valid phone number")
    private String phone;

    private String profilePicture;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Byte strikeCount;

    private List<UserSubscription> userSubscriptions;

    private List<WeightHistory> weightHistory;

    private List<PerformanceHistory> performanceHistoryList;


}
