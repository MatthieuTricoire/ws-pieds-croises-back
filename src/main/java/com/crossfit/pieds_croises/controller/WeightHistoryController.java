package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.WeightHistoryDTO;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.service.WeightHistoryService;
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
public class WeightHistoryController {

    private final WeightHistoryService weightHistoryService;

    @GetMapping
    public ResponseEntity<List<WeightHistoryDTO>> getAllWeightHistoryByUserId(
            @RequestParam(required = false) Integer months,
            @AuthenticationPrincipal User user
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
    public ResponseEntity<WeightHistoryDTO> createWeightHistory(
            @Valid @RequestBody WeightHistoryDTO weightHistoryDTO,
            @AuthenticationPrincipal User user
    ) {
        WeightHistoryDTO savedWeightHistory = weightHistoryService.createWeightHistory(weightHistoryDTO, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedWeightHistory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WeightHistoryDTO> updateWeightHistory(
            @PathVariable Long id,
            @RequestBody WeightHistoryDTO weightHistoryDTO,
            @AuthenticationPrincipal User user
    ) {
        WeightHistoryDTO updatedWeightHistory = weightHistoryService.updateWeightHistory(id, weightHistoryDTO, user.getId());
        return ResponseEntity.ok(updatedWeightHistory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWeightHistory(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        weightHistoryService.deleteWeightHistory(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
