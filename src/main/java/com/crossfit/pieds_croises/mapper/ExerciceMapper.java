package com.crossfit.pieds_croises.mapper;

import com.crossfit.pieds_croises.dto.ExerciceDTO;
import com.crossfit.pieds_croises.model.Exercice;
import com.crossfit.pieds_croises.model.PerformanceHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExerciceMapper {

    @Mapping(source = "performanceHistoryList", target = "performanceHistoryIds")
    ExerciceDTO convertToDTO(Exercice exercice);

    @Mapping(source = "performanceHistoryIds", target = "performanceHistoryList")
    Exercice convertToEntity(ExerciceDTO exerciceDTO);

    default PerformanceHistory map(Long id) {
        return PerformanceHistory.builder()
                .id(id)
                .build();
    }

    default Long map(PerformanceHistory performanceHistory) {
        return performanceHistory.getId();
    }
}
