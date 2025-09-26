package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.MessageCreateDTO;
import com.crossfit.pieds_croises.dto.MessageDTO;
import com.crossfit.pieds_croises.model.Message;
import com.crossfit.pieds_croises.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
@Tag(name = "Message", description = "Gestion des messages et annonces")
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    @Operation(
        summary = "Récupérer tous les messages",
        description = "Récupère tous les messages, avec filtrage optionnel par statut."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Messages récupérés",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageDTO.class))),
        @ApiResponse(responseCode = "204", description = "Aucun message trouvé")
    })
    public ResponseEntity<List<MessageDTO>> getAllMessage(
        @Parameter(description = "Filtrer par statut (active, etc.)", example = "active")
        @RequestParam(required = false) String status) {
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
    @Operation(
        summary = "Récupérer un message par ID",
        description = "Récupère un message spécifique par son identifiant."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Message trouvé",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageDTO.class))),
        @ApiResponse(responseCode = "404", description = "Message non trouvé", content = @Content)
    })
    public ResponseEntity<MessageDTO> getMessageById(
        @Parameter(description = "ID du message", example = "1")
        @PathVariable Long id) {
        MessageDTO messageDTO = messageService.getMessageById(id);
        if (messageDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(messageDTO);
    }


    @PostMapping
    @Operation(
        summary = "Créer un message",
        description = "Crée un nouveau message ou annonce."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Message créé avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    public ResponseEntity<MessageDTO> createMessage(
        @Parameter(description = "Données du message à créer")
        @Valid @RequestBody MessageCreateDTO messageCreateDTO) {
        MessageDTO savedMessage = messageService.createMessage(messageCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMessage);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Mettre à jour un message",
        description = "Met à jour un message existant."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Message mis à jour",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
        @ApiResponse(responseCode = "404", description = "Message non trouvé", content = @Content)
    })
    public ResponseEntity<MessageDTO> updateMessage(
        @Parameter(description = "ID du message", example = "1")
        @PathVariable Long id, 
        @Parameter(description = "Nouvelles données du message")
        @Valid @RequestBody MessageCreateDTO messageCreateDTO) {
        MessageDTO updateMessage = messageService.updateMessage(id, messageCreateDTO);
        if (updateMessage == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updateMessage);
    }

    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Mettre à jour le statut d'un message",
        description = "Met à jour uniquement le statut d'un message existant."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statut mis à jour",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageDTO.class))),
        @ApiResponse(responseCode = "400", description = "Statut invalide", content = @Content),
        @ApiResponse(responseCode = "404", description = "Message non trouvé", content = @Content)
    })
    public ResponseEntity<MessageDTO> updateMessageStatus(
        @Parameter(description = "ID du message", example = "1")
        @PathVariable Long id, 
        @Parameter(description = "Nouveau statut", example = "ACTIVE")
        @RequestParam String status) {
        Message.MessageStatus messageStatus = Message.MessageStatus.valueOf(status.toUpperCase());
        MessageDTO updatedMessage = messageService.updateMessageStatus(id, messageStatus);
        if (updatedMessage == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedMessage);

    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Supprimer un message",
        description = "Supprime un message existant."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Message supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Message non trouvé", content = @Content)
    })
    public ResponseEntity<Void> deleteMessage(
        @Parameter(description = "ID du message", example = "1")
        @PathVariable Long id) {
        if (messageService.deleteMessage(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
