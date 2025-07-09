package com.crossfit.pieds_croises.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Course {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDatetime;

  @Column(columnDefinition = "SMALLINT", nullable = false)
  private short duration;

    @Column(name = "person_limit", columnDefinition = "TINYINT", nullable = false)
    private Integer personLimit;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "courses")
    private List<User> users;

    @ManyToOne
    @JoinColumn(name = "coach_id", nullable = false)
    private User coach;

    public enum Status {
        OPEN,
        FULL,
        CANCELLED;
    }

    public void changeStatus() {
        Integer usersRegisted = getUsers().size();
        if (getPersonLimit().equals(usersRegisted)) {
            setStatus(Status.FULL);
        } else if (getPersonLimit() > usersRegisted) {
            setStatus(Status.OPEN);
        }
    }
}
