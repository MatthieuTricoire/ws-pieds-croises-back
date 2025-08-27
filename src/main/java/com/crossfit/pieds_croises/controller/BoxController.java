package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.dto.BoxDto;
import com.crossfit.pieds_croises.dto.BoxInfoDTO;
import com.crossfit.pieds_croises.service.BoxService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/boxes")
public class BoxController {
  private final BoxService boxService;

  @GetMapping
  public ResponseEntity<List<BoxDto>> getAllBox() {
    List<BoxDto> boxDto = boxService.getAllBoxes();
    return ResponseEntity.ok(boxDto);
  }

  @GetMapping("/first")
  public ResponseEntity<BoxDto> getFirstBox() {
    BoxDto boxDto = boxService.getFirstBox();
    return ResponseEntity.ok(boxDto);
  }

  @GetMapping("/box-info")
  public ResponseEntity<BoxInfoDTO> getBoxInfo() {
    BoxInfoDTO boxInfo = boxService.getBoxInfo();
    return ResponseEntity.ok(boxInfo);
  }

  @GetMapping("/{id}")
  public ResponseEntity<BoxDto> getBoxById(@PathVariable Long id) {
    BoxDto boxDto = boxService.getBoxById(id);
    return ResponseEntity.ok(boxDto);
  }

  @PostMapping
  public ResponseEntity<BoxDto> createBox(@Valid @RequestBody BoxDto box) {
    try {
      BoxDto boxDto = boxService.createBox(box);
      return ResponseEntity.ok(boxDto);
    } catch (Exception e) {
      System.err.println("Error in controller when creating box: " + e.getMessage());
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<BoxDto> updateBox(@PathVariable Long id, @RequestBody BoxDto box) {
    BoxDto boxDto = boxService.updateBox(id, box);
    return ResponseEntity.ok(boxDto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBox(@PathVariable Long id) {
    boxService.deleteBox(id);
    return ResponseEntity.noContent().build();
  }
}
