package com.crossfit.pieds_croises.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class TypicalWeek {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "date-start-validity")
    private LocalDate dateStartValidity;

    @Column(name = "date-end-validity")
    private LocalDate dateEndValidity;

    @ManyToMany
    @JoinTable(
        name = "typical_week_courses",
        joinColumns = @jakarta.persistence.JoinColumn(name = "typical_week_id"),
        inverseJoinColumns = @jakarta.persistence.JoinColumn(name = "course_id")
        )
    private List<TypicalCourse> typicalCourses;
}
