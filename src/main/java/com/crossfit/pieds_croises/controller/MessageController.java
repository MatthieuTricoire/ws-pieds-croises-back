package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.MessageCreateDTO;
import com.crossfit.pieds_croises.dto.MessageDTO;
import com.crossfit.pieds_croises.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<List<MessageDTO>> getAllMessage(@RequestParam(required = false) String status) {
        if ("active".equalsIgnoreCase(status)) {
            return ResponseEntity.ok(messageService.getActiveMessages());
        }
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


    @PostMapping
    public ResponseEntity<MessageDTO> createMessage(@Valid @RequestBody MessageCreateDTO messageCreateDTO) {
        MessageDTO savedMessage = messageService.createMessage(messageCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMessage);
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

}
