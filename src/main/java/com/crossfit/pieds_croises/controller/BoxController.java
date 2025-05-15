package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.model.dto.BoxDto;
import com.crossfit.pieds_croises.service.BoxService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/boxes")
public class BoxController {
    private BoxService boxService;

    @GetMapping
    public ResponseEntity<List<BoxDto>> getAllBox(){
        List<BoxDto> boxDto = boxService.getAllBox();
        if (boxDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(boxDto);
    }

    @GetMapping("/first")
    public ResponseEntity<BoxDto> getFirstBox(){
        BoxDto boxDto = boxService.getFirstBox();
        if (boxDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(boxDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoxDto> getBoxById(@PathVariable Long id){
        BoxDto boxDto = boxService.getBoxById(id);
        if (boxDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(boxDto);
    }

    @PostMapping
    public ResponseEntity<BoxDto> createBox(@Valid @RequestBody BoxDto box){
        BoxDto boxDto = boxService.createBox(box);
        if (boxDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(boxDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoxDto> updateBox(@PathVariable Long id, @RequestParam BoxDto box){
        BoxDto boxDto = boxService.updateBox(id, box);
        if (boxDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(boxDto);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBox(@PathVariable Long id){
        boxService.deleteBox(id);
        return ResponseEntity.noContent().build();
    }
}
