package com.crossfit.pieds_croises.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String firstname;

    @Column(length = 100)
    private String lastname;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 10, unique = true)
    private String phone;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private Integer penalty;

    @Enumerated(EnumType.STRING)
    @Column(name = "suspension_type")
    private SuspensionType suspensionType;

    @Column(name = "suspension_start_date")
    private LocalDateTime suspensionStartDate;

    @Column(name = "suspension_end_date")
    private LocalDateTime suspensionEndDate;

    @OneToMany(mappedBy = "user")
    private List<WeightHistory> weightHistory;

    public enum SuspensionType {
        HOLIDAY,
        PENALTY,
    }

}