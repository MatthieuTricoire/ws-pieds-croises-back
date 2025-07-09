package com.crossfit.pieds_croises.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String firstname;

    @Column(length = 100)
    private String lastname;

    @Column()
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 10, unique = true)
    private String phone;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles = new HashSet<>();

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = true)
    private Byte strikeCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "suspension_type", nullable = true)
    private SuspensionType suspensionType;

    @Column(name = "suspension_start_date", nullable = true)
    private LocalDate suspensionStartDate;

    @Column(name = "suspension_end_date", nullable = true)
    private LocalDate suspensionEndDate;

    @OneToMany(mappedBy = "user")
    private List<UserSubscription> userSubscriptions;

    @Column(nullable = true)
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<WeightHistory> weightHistory;

    @Column(nullable = true)
    @OneToMany(mappedBy = "user")
    private List<PerformanceHistory> performanceHistoryList;

    @ManyToMany
    @JoinTable(
            name = "user_course",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void incrementStrikeCount() {
        if (this.suspensionType == SuspensionType.PENALTY) {
            return;
        }
        if (this.strikeCount == null) {
            this.strikeCount = 1;
        } else if (this.strikeCount < Byte.MAX_VALUE) {
            this.strikeCount++;
        } else {
            throw new IllegalStateException("Maximum penalty strikes reached");
        }
    }

    public void decrementStrikeCount() {
        if (this.suspensionType == SuspensionType.PENALTY) {
            return;
        }
        if (this.strikeCount > 0) {
            this.strikeCount--;
        } else {
            throw new IllegalStateException("No penalty strikes to decrement");
        }
    }

    public void applyHolidaySuspension(int days) {
        this.suspensionType = SuspensionType.HOLIDAY;
        this.setSuspensionStartDate(LocalDate.now());
        this.setSuspensionEndDate(LocalDate.now().plusDays(days));
    }

    public void applyPenaltySuspension(int days) {
        this.suspensionType = SuspensionType.PENALTY;
        this.setSuspensionStartDate(LocalDate.now());
        this.setSuspensionEndDate(LocalDate.now().plusDays(days));
    }

    public boolean isSuspended() {
        if (this.suspensionEndDate == null) {
            return false;
        }
        return LocalDate.now().isAfter(this.suspensionEndDate);
    }

    public void resetStrikeCount() {
        this.strikeCount = null;
    }

    public void resetSuspensionTypeAndDates() {
        this.suspensionStartDate = null;
        this.suspensionEndDate = null;
        this.suspensionType = null;
    }

    public enum SuspensionType {
        HOLIDAY,
        PENALTY,
    }

}