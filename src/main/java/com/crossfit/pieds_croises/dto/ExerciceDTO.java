package com.crossfit.pieds_croises.dto;

import com.crossfit.pieds_croises.model.Exercice;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciceDTO {
    private Long id;
    private String name;
    private Exercice.MeasureType measureType;
    private List<Long> performanceHistoryIds;
}
