package com.crossfit.pieds_croises.mapper;

import com.crossfit.pieds_croises.dto.BoxDto;
import com.crossfit.pieds_croises.model.Box;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")

public interface BoxMapper {
    BoxDto convertToDTO(Box box);

    BoxDto convertToBoxDto(Box box);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Box convertToBoxEntity(BoxDto boxDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateBoxFromDto(BoxDto boxDto, @MappingTarget Box box);
}
