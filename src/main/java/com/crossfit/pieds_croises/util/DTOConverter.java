package com.crossfit.pieds_croises.util;

import com.crossfit.pieds_croises.model.Box;
import com.crossfit.pieds_croises.model.dto.BoxDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DTOConverter {
    BoxDto convertToDTO(Box box);
    Box convertToEntity(BoxDto boxDto);
}
