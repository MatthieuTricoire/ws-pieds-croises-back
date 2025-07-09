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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMapper messageMapper;

    private final MessageRepository messageRepository;

    private final LocalDate today = LocalDate.now();

    public List<MessageDTO> getAllMessages() {
        List<Message> messages = messageRepository.findAll();
        if (messages.isEmpty()) {
            throw new ResourceNotFoundException("There are no messages");
        }
        return messages.stream().map(messageMapper::convertToDto).collect(Collectors.toList());
    }

    public MessageDTO getMessageById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Le message avec l'id " + id + " n'a pas été trouvé"));
        return messageMapper.convertToDto(message);
    }

//    public List<MessageDTO> getCurrentMessagesByBoxID(Long boxId) {
//        List<Message> messages = messageRepository.findCurrentMessagesASCByBoxId(boxId, today);
//
//        if (messages.isEmpty()) {
//            throw new ResourceNotFoundException("There are no current messages");
//        }
//        return messages.stream().map(messageMapper::convertToDto).collect(Collectors.toList());
//    }
//
//    public List<MessageDTO> getExpiredMessages(Long boxId) {
//        List<Message> messages = messageRepository.findExpirationMessagesDESCByBoxId(boxId, today);
//
//        if (messages.isEmpty()) {
//            throw new ResourceNotFoundException("There are no expired messages");
//        }
//
//        return messages.stream().map(messageMapper::convertToDto).collect(Collectors.toList());
//    }
//
//    public List<MessageDTO> getComingMessages(Long boxId) {
//        List<Message> messages = messageRepository.findComingMessagesASCByBoxId(boxId, today);
//
//        if (messages.isEmpty()) {
//            throw new ResourceNotFoundException("There are no coming messages");
//        }
//
//        return messages.stream().map(messageMapper::convertToDto).collect(Collectors.toList());
//    }


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

    public boolean deleteMessage(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));

        if (message == null) {
            return false;
        }

        messageRepository.delete(message);
        return true;
    }

}
