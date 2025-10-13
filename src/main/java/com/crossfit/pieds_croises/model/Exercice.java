package com.crossfit.pieds_croises.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exercice {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "measure_type")
    private MeasureType measureType;

    @OneToMany(mappedBy = "exercice")
    private List<PerformanceHistory> performanceHistoryList;

    public enum MeasureType{
        REPETITION,
        WEIGHT,
    }
}
