package com.crossfit.pieds_croises.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "performance_history")
public class PerformanceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private float measuredValue;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "exercice_id")
    private Exercice exercice;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
