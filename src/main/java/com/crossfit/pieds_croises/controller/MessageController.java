package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.MessageCreateDTO;
import com.crossfit.pieds_croises.dto.MessageDTO;
import com.crossfit.pieds_croises.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<List<MessageDTO>> getAllMessage() {
        List<MessageDTO> messages = messageService.getAllMessages();
        if (messages.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDTO> getMessageById(@PathVariable Long id) {
        MessageDTO messageDTO = messageService.getMessageById(id);
        if (messageDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(messageDTO);
    }

//     TODO methodes get en fonction de la date
//    - message en cour
//    - message future


    @PostMapping
    public ResponseEntity<MessageDTO> createMessage(@Valid @RequestBody MessageCreateDTO messageCreateDTO) {
        try {
            MessageDTO savedMessage = messageService.createMessage(messageCreateDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMessage);
        } catch (Exception e) {
            System.err.println("Error in controller when creating Message: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageDTO> updateMessage(@PathVariable Long id, @Valid @RequestBody MessageCreateDTO messageCreateDTO) {
        MessageDTO updateMessage = messageService.updateMessage(id, messageCreateDTO);
        if (updateMessage == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updateMessage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        if (messageService.deleteMessage(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/expired")
    public ResponseEntity<List<MessageDTO>> getExpiredMessages() {
        List<MessageDTO> messages = messageService.getExpiredMessages();
        if (messages.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(messages);
    }

    @GetMapping("/coming")
    public ResponseEntity<List<MessageDTO>> getComingMessages() {
        List<MessageDTO> messages = messageService.getComingMessages();
        if (messages.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(messages);
    }

}
