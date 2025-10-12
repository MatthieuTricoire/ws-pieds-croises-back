package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.datetime.DateTimeProvider;
import com.crossfit.pieds_croises.dto.MessageCreateDTO;
import com.crossfit.pieds_croises.dto.MessageDTO;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.mapper.MessageMapper;
import com.crossfit.pieds_croises.model.Message;
import com.crossfit.pieds_croises.repository.MessageRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMapper messageMapper;
    private final MessageRepository messageRepository;
    private final DateTimeProvider dateTimeProvider;

    public List<MessageDTO> getAllMessages() {
        List<Message> messages = messageRepository.findAll();
        return messages.stream()
                .map(messageMapper::convertToDto)
                .collect(Collectors.toList());
    }

    public MessageDTO getMessageById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message with id " + id + " not found"));
        return messageMapper.convertToDto(message);
    }

    public List<MessageDTO> getActiveMessages() {
        List<Message> messages = messageRepository.findActiveMessagesOrderByExpirationDateDesc(dateTimeProvider.today());

        if (messages.isEmpty()) {
            throw new ResourceNotFoundException("There are no current messages");
        }
        return messages.stream().map(messageMapper::convertToDto).collect(Collectors.toList());
    }

    public MessageDTO updateMessageStatus(Long id, Message.MessageStatus status) {
        Message existingMessage = messageRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        existingMessage.setMessageStatus(status);
        existingMessage.setUpdatedAt(LocalDateTime.now());

        Message savedMessage = messageRepository.save(existingMessage);
        return messageMapper.convertToDto(savedMessage);
    }


    public MessageDTO createMessage(MessageCreateDTO messageCreateDTO) {
        Message message = messageMapper.convertToEntity(messageCreateDTO);

        Message savedMessage = messageRepository.save(message);
        return messageMapper.convertToDto(savedMessage);
    }

    public MessageDTO updateMessage(Long id, @Valid MessageCreateDTO messageCreateDTO) {
        Message existingMessage = messageRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));

        messageMapper.updateFromDto(messageCreateDTO, existingMessage);
        Message savedMessage = messageRepository.save(existingMessage);
        return messageMapper.convertToDto(savedMessage);
    }

    public void deleteMessage(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        messageRepository.delete(message);
    }
}
