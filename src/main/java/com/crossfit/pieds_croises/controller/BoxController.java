package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.BoxInfoDTO;
import com.crossfit.pieds_croises.service.BoxService;
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
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/box")
@Tag(name = "Box", description = "Gestion des informations de la salle de sport")
public class BoxController {
  private final BoxService boxService;

  @GetMapping("/box-info")
  @Operation(
      summary = "Récupérer les informations de la box",
      description = "Récupère les informations générales de la salle de sport."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Informations récupérées",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = BoxInfoDTO.class)))
  })
  public ResponseEntity<BoxInfoDTO> getBoxInfo() {
    BoxInfoDTO boxInfo = boxService.getBoxInfo();
    return ResponseEntity.ok(boxInfo);
  }

  @PutMapping("/box-info")
  @Operation(
      summary = "Mettre à jour les informations de la box",
      description = "Met à jour les informations générales de la salle de sport."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Informations mises à jour",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = BoxInfoDTO.class))),
      @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
  })
  public ResponseEntity<BoxInfoDTO> updateBox(
      @Parameter(description = "Nouvelles informations de la box")
      @Valid @RequestBody BoxInfoDTO box) {
    BoxInfoDTO boxInfo = boxService.updateBox(box);
    return ResponseEntity.ok(boxInfo);
  }
}
