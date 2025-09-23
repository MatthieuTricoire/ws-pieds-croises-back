package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.dto.BoxInfoDTO;
import com.crossfit.pieds_croises.mapper.BoxMapper;
import com.crossfit.pieds_croises.model.Box;
import com.crossfit.pieds_croises.repository.BoxRepository;
import org.springframework.stereotype.Service;

@Service
public class BoxService {

    private final BoxMapper boxMapper;
    private final BoxRepository boxRepository;

    public BoxService(BoxMapper boxMapper, BoxRepository boxRepository) {
        this.boxMapper = boxMapper;
        this.boxRepository = boxRepository;
    }

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
