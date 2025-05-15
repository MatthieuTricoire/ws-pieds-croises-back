package com.crossfit.pieds_croises.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private int price;

    @Column(name = "session_per_week")
    private int sessionPerWeek;

    @Column(nullable = false)
    private short duration;

    @Column(columnDefinition = "TEXT")
    private String terminationConditions;

    @ManyToOne
    @JoinColumn(name = "box_id")
    private Box box;
}
