package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.MessageCreateDTO;
import com.crossfit.pieds_croises.dto.MessageDTO;
import com.crossfit.pieds_croises.model.Message;
import com.crossfit.pieds_croises.services.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<List<MessageDTO>> getAllMessages() {
        List<MessageDTO> messageDTOs = messageService.getAllMessages();
        if (messageDTOs == null || messageDTOs.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(messageDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDTO> getMessageById(@PathVariable Long id) {
        MessageDTO messageDTO = messageService.getMessageById(id);
        if (messageDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(messageDTO);
    }

    @PostMapping
    public ResponseEntity<MessageDTO> createMessage(@Valid @RequestBody MessageCreateDTO messageCreateDTO) {
        MessageDTO savedMessageDTO = messageService.createMessage(messageCreateDTO);
        if (savedMessageDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMessageDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageDTO> updateMessage(@PathVariable Long id, @RequestBody Message messageDetails) {

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {}

}
