package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.dto.MessageCreateDTO;
import com.crossfit.pieds_croises.dto.MessageDTO;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.mapper.MessageMapper;
import com.crossfit.pieds_croises.model.Message;
import com.crossfit.pieds_croises.repository.MessageRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMapper messageMapper;

    private final MessageRepository messageRepository;

    public List<MessageDTO> getAllMessages() {
        List<Message> messages = messageRepository.findAll();
        return messages.stream().map(messageMapper::convertToDto).collect(Collectors.toList());
    }

    public MessageDTO getMessageById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Le message avec l'id " + id + " n'a pas été trouvé"));
        return messageMapper.convertToDto(message);
    }

    public MessageDTO createMessage(MessageCreateDTO messageCreateDTO) {

        try {
            System.out.println("Message type reçu : " + messageCreateDTO.getMessageType());
            Message message = messageMapper.convertToEntity(messageCreateDTO);

            Message savedMessage = messageRepository.save(message);

            return messageMapper.convertToDto(savedMessage);
        } catch (Exception e) {
            System.err.println("Error creating message: " + e.getMessage());
            throw e;
        }
    }

    public MessageDTO updateMessage(Long id, @Valid MessageCreateDTO messageCreateDTO) {

//       TODO changer le orElse par une exception
        Message existingMessage = messageRepository.findById(id)
                .orElse(null);

        messageMapper.updateFromDto(messageCreateDTO, existingMessage);

        //       TODO changer le orElse par une exception pour enlever l'alert
        Message savedMessage = messageRepository.save(existingMessage);
        return messageMapper.convertToDto(savedMessage);
    }

    public boolean deleteMessage(Long id) {
        //       TODO changer le orElse par une exception
        Message message = messageRepository.findById(id).orElse(null);

        if (message == null) {
            return false;
        }

        messageRepository.delete(message);
        return true;
    }

}
