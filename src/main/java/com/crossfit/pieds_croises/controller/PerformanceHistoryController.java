package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.PerformanceHistoryDTO;
import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.service.PerformanceHistoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/performance-histories")
public class PerformanceHistoryController {

    private final PerformanceHistoryService performanceHistoryService;

    @GetMapping
    public ResponseEntity<List<PerformanceHistoryDTO>> getAllPerformanceHistoryByUserId(
            @RequestParam(required = false) Integer months,
            @AuthenticationPrincipal User user
    ) {
        List<PerformanceHistoryDTO> performanceHistoryDTOS;

        if (months != null){
            performanceHistoryDTOS = performanceHistoryService.getAllPerformanceHistoryForLastXMonths(user.getId(), months);
        } else {
            performanceHistoryDTOS = performanceHistoryService.getAllPerformanceHistory(user.getId());
        }
        return ResponseEntity.ok(performanceHistoryDTOS);
    }

    @PostMapping
    public ResponseEntity<PerformanceHistoryDTO> createPerformanceHistory(
            @Valid @RequestBody PerformanceHistoryDTO performanceHistoryDTO,
            @AuthenticationPrincipal User user
    ) {
        PerformanceHistoryDTO savedPerformanceHistory = performanceHistoryService.createPerformanceHistory(performanceHistoryDTO, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPerformanceHistory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerformanceHistoryDTO> updatePerformanceHistory(
            @PathVariable Long id,
            @RequestBody PerformanceHistoryDTO performanceHistoryDTO,
            @AuthenticationPrincipal User user
    ) {
        PerformanceHistoryDTO updatedPerformanceHistory = performanceHistoryService.updatePerformanceHistory(id, performanceHistoryDTO, user.getId());
        return ResponseEntity.ok(updatedPerformanceHistory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerformanceHistory(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        performanceHistoryService.deletePerformanceHistory(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
