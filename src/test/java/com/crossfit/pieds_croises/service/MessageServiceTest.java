package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.datetime.DateTimeProvider;
import com.crossfit.pieds_croises.dto.MessageCreateDTO;
import com.crossfit.pieds_croises.dto.MessageDTO;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.mapper.MessageMapper;
import com.crossfit.pieds_croises.model.Message;
import com.crossfit.pieds_croises.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class MessageServiceTest {

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private DateTimeProvider dateTimeProvider;

    @InjectMocks
    private MessageService messageService;

    @Test
    public void testGetAllMessages() {
        // Arrange
        Message message1 = new Message();
        Message message2 = new Message();
        List<Message> messages = List.of(message1, message2);
        MessageDTO messageDTO1 = new MessageDTO();
        MessageDTO messageDTO2 = new MessageDTO();

        when(messageRepository.findAll()).thenReturn(messages);
        when(messageMapper.convertToDto(message1)).thenReturn(messageDTO1);
        when(messageMapper.convertToDto(message2)).thenReturn(messageDTO2);

        // Act
        List<MessageDTO> result = messageService.getAllMessages();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(messageDTO1, messageDTO2);
        verify(messageRepository, times(1)).findAll();
        verify(messageMapper, times(1)).convertToDto(message1);
        verify(messageMapper, times(1)).convertToDto(message2);
    }

    @Test
    public void testGetMessageById() {
        // Arrange
        Long messageId = 1L;
        Message message = new Message();
        MessageDTO expectedMessageDTO = new MessageDTO();

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(messageMapper.convertToDto(message)).thenReturn(expectedMessageDTO);

        // Act
        MessageDTO result = messageService.getMessageById(messageId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedMessageDTO);
        verify(messageRepository, times(1)).findById(messageId);
        verify(messageMapper, times(1)).convertToDto(message);
    }

    @Test
    public void testGetMessageById_WhenMessageNotFound_ShouldThrownException() {
        // Arrange
        Long messageId = 1L;

        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> messageService.getMessageById(messageId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Message with id " + messageId + " not found");
        verify(messageRepository, times(1)).findById(messageId);
        verifyNoMoreInteractions(messageRepository);
    }

    @Test
    public void testCreateMessage() {
        // Arrange
        MessageCreateDTO inputMessageCreateDTO = new MessageCreateDTO();
        Message message = new Message();
        Message savedMessage = new Message();
        MessageDTO expectedMessageDTO = new MessageDTO();

        when(messageMapper.convertToEntity(inputMessageCreateDTO)).thenReturn(message);
        when(messageRepository.save(message)).thenReturn(savedMessage);
        when(messageMapper.convertToDto(savedMessage)).thenReturn(expectedMessageDTO);

        // Act
        MessageDTO result = messageService.createMessage(inputMessageCreateDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedMessageDTO);
        verify(messageMapper, times(1)).convertToEntity(inputMessageCreateDTO);
        verify(messageRepository, times(1)).save(message);
        verify(messageMapper, times(1)).convertToDto(savedMessage);
    }

    @Test
    public void testUpdateMessage() {
        // Arrange
        Long id = 1L;
        MessageCreateDTO inputMessageCreateDTO = new MessageCreateDTO();
        Message existingMessage = new Message();
        Message savedMessage = new Message();
        MessageDTO expectedMessageDTO = new MessageDTO();

        when(messageRepository.findById(id)).thenReturn(Optional.of(existingMessage));
        doNothing().when(messageMapper).updateFromDto(inputMessageCreateDTO, existingMessage);
        when(messageRepository.save(existingMessage)).thenReturn(savedMessage);
        when(messageMapper.convertToDto(savedMessage)).thenReturn(expectedMessageDTO);

        // Act
        MessageDTO result = messageService.updateMessage(id, inputMessageCreateDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedMessageDTO);
        verify(messageRepository, times(1)).findById(id);
        verify(messageMapper, times(1)).updateFromDto(inputMessageCreateDTO, existingMessage);
        verify(messageRepository, times(1)).save(existingMessage);
        verify(messageMapper, times(1)).convertToDto(savedMessage);
    }

    @Test
    public void testUpdateMessage_WhenMessageNotFound_ShouldThrownException() {
        // Arrange
        Long id = 1L;
        MessageCreateDTO inputMessageCreateDTO = new MessageCreateDTO();

        when(messageRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> messageService.updateMessage(id, inputMessageCreateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Message not found with id: " + id);
        verify(messageRepository, times(1)).findById(id);
        verifyNoMoreInteractions(messageRepository);
        verifyNoInteractions(messageMapper);
    }

    @Test
    public void testDeleteMessage() {
        // Arrange
        Long messageId = 1L;
        Message message = new Message();

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

        // Act
        messageService.deleteMessage(messageId);

        // Assert
        verify(messageRepository, times(1)).findById(messageId);
        verify(messageRepository, times(1)).delete(message);
    }

    @Test
    public void testDeleteMessage_WhenMessageNotFound_ShouldThrownException() {
        // Arrange
        Long messageId = 1L;

        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> messageService.deleteMessage(messageId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Message not found with id: " + messageId);
        verify(messageRepository, times(1)).findById(messageId);
        verifyNoMoreInteractions(messageRepository);
    }
}
