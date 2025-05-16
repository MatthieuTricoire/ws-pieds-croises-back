package com.crossfit.pieds_croises.mapper;

import com.crossfit.pieds_croises.dto.MessageCreateDTO;
import org.mapstruct.Mapper;
import com.crossfit.pieds_croises.dto.MessageDTO;
import com.crossfit.pieds_croises.model.Message;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    MessageDTO convertToDto(Message message);

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Message convertToEntity(MessageCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateFromDto(MessageCreateDTO dto, @MappingTarget Message entity);
}
