package com.crossfit.pieds_croises.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;



@Entity
public class User{

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column( length = 100)
    private String firstname;

    @Column( length = 100)
    private String lastname;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 10, unique = true)
    private String phone;

    @Column(name="profile_picture")
    private String profilePicture;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private Integer penalty;

    @Enumerated(EnumType.STRING)
    @Column(name = "suspension_type", columnDefinition = "ENUM('HOLIDAY', 'PENALTY')")
    private SuspensionType suspensionType;

    @Column(name = "suspension_start_date")
    private LocalDateTime suspensionStartDate;

    @Column(name = "suspension_end_date")
    private LocalDateTime suspensionEndDate;

    public enum SuspensionType{
        HOLIDAY,
        PENALTY,
    }

    // Getters and Setters

    public LocalDateTime getSuspensionEndDate() {
        return suspensionEndDate;
    }

    public void setSuspensionEndDate(LocalDateTime suspensionEndDate) {
        this.suspensionEndDate = suspensionEndDate;
    }

    public LocalDateTime getSuspensionStartDate() {
        return suspensionStartDate;
    }

    public void setSuspensionStartDate(LocalDateTime suspensionStartDate) {
        this.suspensionStartDate = suspensionStartDate;
    }

    public SuspensionType getSuspensionType() {
        return suspensionType;
    }

    public void setSuspensionType(SuspensionType suspensionType) {
        this.suspensionType = suspensionType;
    }

    public Integer getPenalty() {
        return penalty;
    }

    public void setPenalty(Integer penalty) {
        this.penalty = penalty;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
