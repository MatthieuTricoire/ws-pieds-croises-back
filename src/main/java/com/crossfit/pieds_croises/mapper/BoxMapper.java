package com.crossfit.pieds_croises.mapper;

import com.crossfit.pieds_croises.dto.BoxInfoDTO;
import com.crossfit.pieds_croises.model.Box;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BoxMapper {
    BoxInfoDTO convertToBoxInfoDTO(Box box);

    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateBoxFromDTO(BoxInfoDTO boxDto, @MappingTarget Box box);
}
