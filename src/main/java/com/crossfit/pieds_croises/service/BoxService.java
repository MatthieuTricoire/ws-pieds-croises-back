package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.dto.BoxDto;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.mapper.BoxMapper;
import com.crossfit.pieds_croises.model.Box;
import com.crossfit.pieds_croises.repository.BoxRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class BoxService {

    private final BoxMapper boxMapper;
    private final BoxRepository boxRepository;

    public List<BoxDto> getAllBox() {
        List<Box> boxes = boxRepository.findAll();
        if (boxes.isEmpty()) {
            throw new ResourceNotFoundException("No boxes found");
        }
        return boxes.stream()
                .map(boxMapper::convertToDTO)
                .toList();
    }

    public BoxDto getFirstBox() {
        List<Box> boxes = boxRepository.findAll();
        if (boxes.isEmpty()) {
            throw new ResourceNotFoundException("No boxes found");
        }
        Box firstBox = boxes.getFirst();
        return boxMapper.convertToDTO(firstBox);
    }

    public BoxDto getBoxById(Long id) {
        Box box = boxRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Box not found with id: " + id));
        return boxMapper.convertToDTO(box);
    }

    public BoxDto createBox(BoxDto boxDto) {
        try {
            Box box = boxMapper.convertToEntity(boxDto);
            box.setCreatedAt(LocalDateTime.now());
            box.setUpdatedAt(LocalDateTime.now());
            Box createdBox = boxRepository.save(box);
            return boxMapper.convertToDTO(createdBox);
        } catch (Exception e) {
            System.err.println("Error creating box: " + e.getMessage());
            throw e;
        }
    }

    public BoxDto updateBox(Long id, BoxDto boxDto) {
        Box existingBox = boxRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Box not found with id: " + id));
        if (boxDto.getName() != null) {
            existingBox.setName(boxDto.getName());
        }
        if (boxDto.getAddress() != null) {
            existingBox.setAddress(boxDto.getAddress());
        }
        if (boxDto.getCity() != null) {
            existingBox.setCity(boxDto.getCity());
        }
        if (boxDto.getZipcode() != null) {
            existingBox.setZipcode(boxDto.getZipcode());
        }
        if (boxDto.getSchedule() != null) {
            existingBox.setSchedule(boxDto.getSchedule());
        }

        Box updatedBox = boxRepository.save(existingBox);
        return boxMapper.convertToDTO(updatedBox);
    }

    public void deleteBox(Long id) {
        Box box = boxRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Box not found with id: " + id));
        boxRepository.delete(box);
    }


}
