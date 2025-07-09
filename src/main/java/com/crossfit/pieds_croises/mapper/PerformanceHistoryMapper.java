package com.crossfit.pieds_croises.mapper;

import com.crossfit.pieds_croises.dto.PerformanceHistoryDTO;
import com.crossfit.pieds_croises.model.PerformanceHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PerformanceHistoryMapper {

    @Mapping(source = "exercice.id", target = "exerciseId")
    PerformanceHistoryDTO convertToDTO(PerformanceHistory performanceHistory);

    @Mapping(source = "exerciseId", target = "exercice.id")
    PerformanceHistory convertToEntity(PerformanceHistoryDTO performanceHistoryDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercice", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromDTO(PerformanceHistoryDTO performanceHistoryDTO, @MappingTarget PerformanceHistory performanceHistory);
}
