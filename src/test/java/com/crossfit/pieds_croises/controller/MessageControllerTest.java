package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.MessageCreateDTO;
import com.crossfit.pieds_croises.dto.MessageDTO;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.security.CustomUserDetailsService;
import com.crossfit.pieds_croises.security.JwtAuthenticationFilter;
import com.crossfit.pieds_croises.security.JwtService;
import com.crossfit.pieds_croises.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    public void testGetAllMessages() throws Exception {
        // Arrange
        MessageDTO message1 = new MessageDTO();
        message1.setTitle("Title 1");
        MessageDTO message2 = new MessageDTO();
        message2.setTitle("Title 2");

        when(messageService.getAllMessages())
                .thenReturn(List.of(message1,message2));

        // Act & Assert
        mockMvc.perform(get("/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Title 1"))
                .andExpect(jsonPath("$[1].title").value("Title 2"));
    }

    @Test
    public void testGetAllMessages_returnsNoContent_whenNoMessage() throws Exception {
        // Arrange
        when(messageService.getAllMessages())
                .thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/messages"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetMessageById() throws Exception {
        // Arrange
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setTitle("Title 1");

        when(messageService.getMessageById(1L))
                .thenReturn(messageDTO);

        // Act & Assert
        mockMvc.perform(get("/messages/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title 1"));
    }

    @Test
    public void testGetMessageById_returnsNoContent_whenMessageNotFound() throws Exception {
        // Arrange
        when(messageService.getMessageById(99L))
                .thenThrow(new ResourceNotFoundException("Message with id 99 not found"));

        // Act & Assert
        mockMvc.perform(get("/messages/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Message with id 99 not found"));
    }

    @Test
    public void testCreateMessage() throws Exception {
        // Arrange
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        MessageDTO savedMessage = new MessageDTO();
        savedMessage.setTitle("Message title");
        String json = String.format("""
        {
            "title": "Message title",
            "content": "Message content",
            "messageType": "INFORMATION",
            "startDate": "%s",
            "expirationDate": "%s"
        }
        """,  today, tomorrow);

        when(messageService.createMessage(any(MessageCreateDTO.class)))
                .thenReturn(savedMessage);

        // Act & Assert
        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Message title"));
    }

    @Test
    public void testCreateMessage_returnsBadRequest_whenMissingFields() throws Exception {
        // Arrange
        String json = "{}";

        // Act & Assert
        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.messageType").exists());
    }

    @Test
    public void testUpdateMessage() throws Exception {
        // Arrange
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        MessageDTO updatedMessage = new MessageDTO();
        updatedMessage.setTitle("Updated message");
        String json = String.format("""
        {
            "title": "Updated message",
            "content": "Updated content",
            "messageType": "INFORMATION",
            "startDate": "%s",
            "expirationDate": "%s"
        }
        """,  today, tomorrow);

        when(messageService.updateMessage(eq(1L), any(MessageCreateDTO.class)))
                .thenReturn(updatedMessage);

        // Act & Assert
        mockMvc.perform(put("/messages/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated message"));
    }

    @Test
    public void testUpdateMessage_returnsBadRequest_whenMissingFields() throws Exception {
        // Arrange
        String json = "{}";

        // Act & Assert
        mockMvc.perform(put("/messages/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.messageType").exists());
    }

    @Test
    public void testUpdateMessage_returnsNoContent_whenMessageNotFound() throws Exception {
        // Arrange
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        String json = String.format("""
        {
            "title": "Updated message",
            "content": "Updated content",
            "messageType": "INFORMATION",
            "startDate": "%s",
            "expirationDate": "%s"
        }
        """,  today, tomorrow);

        when( messageService.updateMessage(eq(99L), any(MessageCreateDTO.class)))
                .thenThrow(new ResourceNotFoundException("Message not found with id: 99"));

        // Act
        mockMvc.perform(put("/messages/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Message not found with id: 99"));
    }

    @Test
    public void testDeleteMessage() throws Exception {
        // Arrange
        doNothing().when(messageService).deleteMessage(eq(1L));

        // Act & Assert
        mockMvc.perform(delete("/messages/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteMessage_returnsNotFound_whenMessageNotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Message not found with id: 99")).when(messageService).deleteMessage(eq(99L));

        // Act & Assert
        mockMvc.perform(delete("/messages/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Message not found with id: 99"));
    }
}