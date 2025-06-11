package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.dto.BoxDto;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.mapper.BoxMapper;
import com.crossfit.pieds_croises.model.Box;
import com.crossfit.pieds_croises.repository.BoxRepository;

import lombok.AllArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class BoxService {

  private final BoxMapper boxMapper;
  private final BoxRepository boxRepository;

  public BoxService(BoxMapper boxMapper, BoxRepository boxRepository) {
    this.boxMapper = boxMapper;
    this.boxRepository = boxRepository;
  }

  public List<BoxDto> getAllBoxes() {
    List<Box> boxes = boxRepository.findAll();
    if (boxes.isEmpty()) {
      throw new ResourceNotFoundException("No boxes found");
    }
    return boxes.stream()
        .map(boxMapper::convertToBoxDto)
        .toList();
  }

  public BoxDto getFirstBox() {
    List<Box> boxes = boxRepository.findAll();
    if (boxes.isEmpty()) {
      throw new ResourceNotFoundException("No boxes found");
    }
    Box firstBox = boxes.getFirst();
    return boxMapper.convertToBoxDto(firstBox);
  }

  public BoxDto getBoxById(Long id) {
    Box box = boxRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Box not found with id: " + id));
    return boxMapper.convertToBoxDto(box);
  }

  public BoxDto createBox(BoxDto boxDto) {
    try {
      Box box = boxMapper.convertToBoxEntity(boxDto);
      Box createdBox = boxRepository.save(box);
      return boxMapper.convertToBoxDto(createdBox);
    } catch (Exception e) {
      System.err.println("Error creating box: " + e.getMessage());
      throw new RuntimeException("Error creating box", e);
    }
  }

  public BoxDto updateBox(Long id, BoxDto boxDto) {
    Box existingBox = boxRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Box not found with id: " + id));
    boxMapper.updateBoxFromDto(boxDto, existingBox);
    Box updatedBox = boxRepository.save(existingBox);
    return boxMapper.convertToBoxDto(updatedBox);
  }

  public void deleteBox(Long id) {
    Box box = boxRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Box not found with id: " + id));
    boxRepository.delete(box);
  }



}
