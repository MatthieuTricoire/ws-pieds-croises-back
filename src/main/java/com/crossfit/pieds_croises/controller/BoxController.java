package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.BoxInfoDTO;
import com.crossfit.pieds_croises.service.BoxService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/box")
public class BoxController {
  private final BoxService boxService;

  @GetMapping("/box-info")
  public ResponseEntity<BoxInfoDTO> getBoxInfo() {
    BoxInfoDTO boxInfo = boxService.getBoxInfo();
    return ResponseEntity.ok(boxInfo);
  }

  @PutMapping("/box-info")
  public ResponseEntity<BoxInfoDTO> updateBox(@Valid @RequestBody BoxInfoDTO box) {
    BoxInfoDTO boxInfo = boxService.updateBox(box);
    return ResponseEntity.ok(boxInfo);
  }
}
