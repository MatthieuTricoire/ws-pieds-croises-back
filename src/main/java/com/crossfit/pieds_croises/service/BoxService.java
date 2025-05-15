package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.model.Box;
import com.crossfit.pieds_croises.model.dto.BoxDto;
import com.crossfit.pieds_croises.repository.BoxRepository;
import com.crossfit.pieds_croises.util.DTOConverter;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@AllArgsConstructor
@Service
@Validated
public class BoxService {
  private final DTOConverter dtoConverter;
  private final BoxRepository boxRepository;



    public List<BoxDto> getAllBox() {
        List<Box>boxes = boxRepository.findAll();
        if(boxes.isEmpty()) {
            throw new ResourceNotFoundException("No boxes found");
        }
        return boxes.stream()
                .map(dtoConverter::convertToDTO)
                .toList();
    }

    public BoxDto getFirstBox(){
        List<Box> boxes = boxRepository.findAll();
            if( boxes.isEmpty()) {
                throw new ResourceNotFoundException("No boxes found");
            }
            Box firstBox = boxes.getFirst();
        return dtoConverter.convertToDTO(firstBox);
    }

    public BoxDto getBoxById(Long id){
        Box box = boxRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Box not found with id: " + id));
        return dtoConverter.convertToDTO(box);
    }

    public BoxDto createBox(@Valid BoxDto boxDto){
        Box box = dtoConverter.convertToEntity(boxDto);
        Box createdBox = boxRepository.save(box);
        return dtoConverter.convertToDTO(createdBox);
    }

    public BoxDto updateBox(Long id, BoxDto boxDto){
        Box existingBox = boxRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Box not found with id: " + id));
        if(boxDto.getName() != null) {
            existingBox.setName(boxDto.getName());
        }
        if(boxDto.getAddress() != null) {
            existingBox.setAddress(boxDto.getAddress());
        }
        if(boxDto.getCity() != null) {
            existingBox.setCity(boxDto.getCity());
        }
        if(boxDto.getZipcode() != null) {
            existingBox.setZipcode(boxDto.getZipcode());
        }
        if(boxDto.getSchedule() != null) {
            existingBox.setSchedule(boxDto.getSchedule());
        }

        Box updatedBox = boxRepository.save(existingBox);
        return dtoConverter.convertToDTO(updatedBox);
    }

    public void deleteBox(Long id) {
        Box box = boxRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Box not found with id: " + id));
        boxRepository.delete(box);
    }


}
