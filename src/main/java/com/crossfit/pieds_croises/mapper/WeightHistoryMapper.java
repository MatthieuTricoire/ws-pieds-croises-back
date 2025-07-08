package com.crossfit.pieds_croises.mapper;

import com.crossfit.pieds_croises.dto.WeightHistoryDTO;
import com.crossfit.pieds_croises.model.WeightHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WeightHistoryMapper {

    WeightHistoryDTO convertToDTO(WeightHistory weightHistory);

    WeightHistory convertToEntity(WeightHistoryDTO weightHistoryDTO);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(WeightHistoryDTO weightHistoryDTO, @MappingTarget WeightHistory weightHistory);

}
