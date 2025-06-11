package com.crossfit.pieds_croises.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Box {

  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  private Long id;

  @Column(length = 100, nullable = false)
  private String name;

  @Column()
  private String address;

  @Column(length = 100)
  private String city;

  @Column(length = 5, nullable = false)
  private String zipcode;

  @Column(columnDefinition = "TEXT")
  private String schedule;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "box", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
  private List<Subscription> subscriptions = new java.util.ArrayList<>();

  @OneToMany(mappedBy = "box", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
  private List<Message> messages = new java.util.ArrayList<>();

  @OneToMany(mappedBy = "box", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
  private List<User> users = new java.util.ArrayList<>();

}
