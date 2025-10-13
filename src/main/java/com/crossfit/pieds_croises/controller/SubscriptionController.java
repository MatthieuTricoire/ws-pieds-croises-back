package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.SubscriptionCreateDto;
import com.crossfit.pieds_croises.dto.SubscriptionDto;
import com.crossfit.pieds_croises.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/subscriptions")
@Tag(name = "Subscription", description = "Gestion des types d'abonnements")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @GetMapping
    @Operation(
        summary = "Récupérer tous les abonnements",
        description = "Récupère la liste de tous les types d'abonnements disponibles."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Abonnements récupérés",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubscriptionDto.class)))
    })
    public ResponseEntity<List<SubscriptionDto>> getAllSubscriptions() {
        List<SubscriptionDto> subscriptionDtos = subscriptionService.getAllSubscriptions();
        return ResponseEntity.ok(subscriptionDtos);
    }

    @PostMapping
    @Operation(
        summary = "Créer un abonnement",
        description = "Crée un nouveau type d'abonnement."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Abonnement créé",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubscriptionDto.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    public ResponseEntity<SubscriptionDto> addSubscription(
        @Parameter(description = "Données de l'abonnement à créer")
        @Valid @RequestBody SubscriptionCreateDto subscriptionCreateDto) {
        SubscriptionDto createdSubscription = subscriptionService.addSubscription(subscriptionCreateDto);
        return ResponseEntity.ok().body(createdSubscription);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Récupérer un abonnement par ID",
        description = "Récupère un type d'abonnement spécifique."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Abonnement trouvé",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubscriptionDto.class))),
        @ApiResponse(responseCode = "404", description = "Abonnement non trouvé", content = @Content)
    })
    public ResponseEntity<SubscriptionDto> getSubscriptionById(
        @Parameter(description = "ID de l'abonnement", example = "1")
        @PathVariable Long id) {
        SubscriptionDto subscriptionDto = subscriptionService.getSubscriptionById(id);
        if (subscriptionDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(subscriptionDto);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Supprimer un abonnement",
        description = "Supprime un type d'abonnement."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Abonnement supprimé"),
        @ApiResponse(responseCode = "404", description = "Abonnement non trouvé", content = @Content)
    })
    public ResponseEntity<Void> deleteSubscription(
        @Parameter(description = "ID de l'abonnement", example = "1")
        @PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Mettre à jour un abonnement",
        description = "Met à jour un type d'abonnement existant."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Abonnement mis à jour",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubscriptionDto.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
        @ApiResponse(responseCode = "404", description = "Abonnement non trouvé", content = @Content)
    })
    public ResponseEntity<SubscriptionDto> updateSubscription(
        @Parameter(description = "ID de l'abonnement", example = "1")
        @PathVariable Long id, 
        @Parameter(description = "Nouvelles données de l'abonnement")
        @Valid @RequestBody SubscriptionDto subscriptionDto) {
        SubscriptionDto updatedSubscription = subscriptionService.updateSubscription(id, subscriptionDto);
        if (updatedSubscription == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedSubscription);
    }

}
