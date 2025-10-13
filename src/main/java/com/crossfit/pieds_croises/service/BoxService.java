package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.dto.BoxInfoDTO;
import com.crossfit.pieds_croises.mapper.BoxMapper;
import com.crossfit.pieds_croises.model.Box;
import com.crossfit.pieds_croises.repository.BoxRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BoxService {

    private final BoxMapper boxMapper;
    private final BoxRepository boxRepository;

    public BoxInfoDTO getBoxInfo() {
        Box box = boxRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No box found"));
        return boxMapper.convertToBoxInfoDTO(box);
    }

    public BoxInfoDTO updateBox(BoxInfoDTO boxDTO) {
        Box existingBox = boxRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No box found"));
        boxMapper.updateBoxFromDTO(boxDTO, existingBox);
        boxRepository.save(existingBox);
        return boxMapper.convertToBoxInfoDTO(existingBox);
    }
}
