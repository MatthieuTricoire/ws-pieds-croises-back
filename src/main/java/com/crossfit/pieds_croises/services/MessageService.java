package com.crossfit.pieds_croises.services;

import com.crossfit.pieds_croises.dto.MessageCreateDTO;
import com.crossfit.pieds_croises.dto.MessageDTO;
import com.crossfit.pieds_croises.mapper.MessageMapper;
import com.crossfit.pieds_croises.model.Message;
import com.crossfit.pieds_croises.repository.MessageRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Validated
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    public List<MessageDTO> getAllMessages() {
        List<Message> messages = messageRepository.findAll();
        if(messages.isEmpty()) {
            throw new ResourceNotFoundException("No boxes found");
        }
        return messages.stream()
                .map(messageMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    public MessageDTO getMessageById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Le message avec l'id " + id + " n'a pas été trouvé"));
        return messageMapper.convertToDTO(message);
    }

    public MessageDTO createMessage(MessageDTO messageDTO) {
        try {
            Message message = dtoConverter.convertToEntity(messageDTO);
            message.setCreatedAt(LocalDateTime.now());
            message.setUpdatedAt(LocalDateTime.now());
            message createdMessage = messageRepository.save(message);
            return dtoConverter.convertToDTO(createdMessage);
        } catch (Exception e) {
            System.err.println("Error creating message: " + e.getMessage());
            throw e;
        }
    }

    public MessageDTO updateMessage(Long id, MessageDTO messageDTO) {
        Message existingBMessage = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        if(messageDTO.getName() != null) {
            existingBMessage.setName(boxDto.getName());
        }
        if(messageDTO.getAddress() != null) {
            existingBMessage.setAddress(boxDto.getAddress());
        }
        if(messageDTO.getCity() != null) {
            existingBMessage.setCity(boxDto.getCity());
        }
        if(messageDTO.getZipcode() != null) {
            existingBMessage.setZipcode(boxDto.getZipcode());
        }
        if(messageDTO.getSchedule() != null) {
            existingBMessage.setSchedule(boxDto.getSchedule());
        }

        Message updatedMessage = messageRepository.save(existingBMessage);
        return dtoConverter.convertToDTO(updatedBox);
    }

}
