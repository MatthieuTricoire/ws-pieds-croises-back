package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.WeightHistoryDTO;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.service.WeightHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/weight-histories")
@Tag(name = "Weight History", description = "Gestion de l'historique de poids des utilisateurs")
public class WeightHistoryController {

    private final WeightHistoryService weightHistoryService;

    @GetMapping
    @Operation(
        summary = "Récupérer l'historique de poids",
        description = "Récupère l'historique de poids de l'utilisateur connecté. Peut être filtré par nombre de mois."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Historique récupéré avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = WeightHistoryDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erreur serveur", content = @Content)
    })
    public ResponseEntity<List<WeightHistoryDTO>> getAllWeightHistoryByUserId(
            @Parameter(description = "Nombre de mois à récupérer (optionnel)", example = "6")
            @RequestParam(required = false) Integer months,
            @Parameter(hidden = true) @AuthenticationPrincipal User user
    ) {
        List<WeightHistoryDTO> weightHistoryDTOS;

        if (months != null){
            weightHistoryDTOS = weightHistoryService.getAllWeightHistoryForLastXMonths(user.getId(), months);
        } else {
            weightHistoryDTOS = weightHistoryService.getAllWeightHistory(user.getId());
        }
        return ResponseEntity.ok(weightHistoryDTOS);
    }

    @PostMapping
    @Operation(
        summary = "Créer une entrée d'historique de poids",
        description = "Crée une nouvelle entrée dans l'historique de poids pour l'utilisateur connecté."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Entrée créée avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = WeightHistoryDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erreur serveur", content = @Content)
    })
    public ResponseEntity<WeightHistoryDTO> createWeightHistory(
            @Parameter(description = "Données de l'historique de poids à créer")
            @Valid @RequestBody WeightHistoryDTO weightHistoryDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal User user
    ) {
        WeightHistoryDTO savedWeightHistory = weightHistoryService.createWeightHistory(weightHistoryDTO, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedWeightHistory);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Mettre à jour une entrée d'historique de poids",
        description = "Met à jour une entrée existante dans l'historique de poids de l'utilisateur connecté."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Entrée mise à jour avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = WeightHistoryDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
        @ApiResponse(responseCode = "404", description = "Entrée non trouvée", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erreur serveur", content = @Content)
    })
    public ResponseEntity<WeightHistoryDTO> updateWeightHistory(
            @Parameter(description = "ID de l'entrée à mettre à jour", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nouvelles données de l'historique de poids")
            @Valid @RequestBody WeightHistoryDTO weightHistoryDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal User user
    ) {
        WeightHistoryDTO updatedWeightHistory = weightHistoryService.updateWeightHistory(id, weightHistoryDTO, user.getId());
        return ResponseEntity.ok(updatedWeightHistory);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Supprimer une entrée d'historique de poids",
        description = "Supprime une entrée de l'historique de poids de l'utilisateur connecté."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Entrée supprimée avec succès"),
        @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
        @ApiResponse(responseCode = "404", description = "Entrée non trouvée", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erreur serveur", content = @Content)
    })
    public ResponseEntity<Void> deleteWeightHistory(
            @Parameter(description = "ID de l'entrée à supprimer", example = "1")
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal User user
    ) {
        weightHistoryService.deleteWeightHistory(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
