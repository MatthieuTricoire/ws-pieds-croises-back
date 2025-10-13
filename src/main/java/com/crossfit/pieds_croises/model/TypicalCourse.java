package com.crossfit.pieds_croises.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypicalCourse {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "day")
    private DayOfWeek day;

    @Column()
    private LocalTime hour;

    @Column(nullable = false)
    private short duration;

    @Column(name = "person_limit", nullable = false)
    private byte personLimit;

    @ManyToMany(mappedBy = "typicalCourses")
    private List<TypicalWeek> typicalWeeks;

    public enum DayOfWeek {
        LUNDI,
        MARDI,
        MERCREDI,
        JEUDI,
        VENDREDI,
        SAMEDI,
        DIMANCHE
    }
}