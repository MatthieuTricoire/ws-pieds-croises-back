package com.crossfit.pieds_croises.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Arrays;
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

  @Column(nullable = false)
  private LocalDateTime start_datetime;

  @Column(columnDefinition = "SMALLINT", nullable = false)
  private short duration;

  @Column(columnDefinition = "TINYINT", nullable = false)
  private Integer person_limit;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;

  public enum Status {
    OUVERT("ouvert"),
    COMPLET("complet"),
    ANNULE("annulé");

    private final String label;

    Status(String label) {
      this.label = label;
    }

    @JsonValue
    public String getLabel() {
      return label;
    }

    @JsonCreator
    public static Status fromLabel(String label) {
      return Arrays.stream(Status.values())
          .filter(status -> status.label.equalsIgnoreCase(label))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Invalid status: " + label));
    }
  }

  @Column(nullable = false, updatable = false)
  private LocalDateTime created_at;

  @Column(nullable = false)
  private LocalDateTime updated_at;

  @ManyToMany(mappedBy = "courses")
  private List<User> users;
}
