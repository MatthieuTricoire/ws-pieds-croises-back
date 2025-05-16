package com.crossfit.pieds_croises.util;

import com.crossfit.pieds_croises.dto.BoxDto;
import com.crossfit.pieds_croises.model.Box;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DTOConverter {


    BoxDto convertToDTO(Box box);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "subscriptions", expression = "java(new java.util.ArrayList<>())")
    Box convertToEntity(BoxDto boxDto);
}
