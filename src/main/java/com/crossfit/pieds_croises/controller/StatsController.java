package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.MonthlyStatsDto;
import com.crossfit.pieds_croises.dto.StatsDto;
import com.crossfit.pieds_croises.service.StatsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/toremove/stats")
@PreAuthorize("hasRole('ROLE_ADMIN')")

public class StatsController {
  private final StatsService statsService;

  @GetMapping("/current-month")
  public ResponseEntity<StatsDto> getCurrentMonthStats() {
    StatsDto statsDto = statsService.getCurrentMonthStats();
    return ResponseEntity.ok(statsDto);
  }

  @GetMapping("/monthly")
  public ResponseEntity<MonthlyStatsDto> getMonthlyStats(
      @RequestParam int year,
      @RequestParam int month) {
    MonthlyStatsDto statsDto = statsService.getMonthlyStats(year, month);
    return ResponseEntity.ok(statsDto);
  }
}
