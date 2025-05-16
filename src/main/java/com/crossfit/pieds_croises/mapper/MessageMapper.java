package com.crossfit.pieds_croises.mapper;

import com.crossfit.pieds_croises.model.Message;
import com.crossfit.pieds_croises.dto.MessageDTO;
import com.crossfit.pieds_croises.dto.MessageCreateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageDTO convertToDTO(Message message);

    List<MessageDTO> toDtoList(List<Message> messages);

    @Mapping(target = "id", ignore = true)
    Message convertToEntity(MessageCreateDTO messageCreateDTO);

}