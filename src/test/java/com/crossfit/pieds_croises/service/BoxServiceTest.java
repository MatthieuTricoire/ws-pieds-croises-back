package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.datetime.DateTimeProvider;
import com.crossfit.pieds_croises.dto.BoxInfoDTO;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.mapper.BoxMapper;
import com.crossfit.pieds_croises.model.Box;
import com.crossfit.pieds_croises.repository.BoxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class BoxServiceTest {

    @Mock
    private BoxRepository boxRepository;

    @Mock
    private BoxMapper boxMapper;

    @Mock
    private DateTimeProvider dateTimeProvider;

    @InjectMocks
    private BoxService boxService;

//    private Box box1;
//    private Box box2;
//    private BoxInfoDTO BoxInfoDTO1;
//    private BoxInfoDTO BoxInfoDTO2;
//    private Box existingBox;
//    private BoxInfoDTO BoxInfoDTO;
//    private Box updatedBox;
//    private BoxInfoDTO updatedBoxInfoDTO;
//
//    @BeforeEach
//    public void setUp() {
//        box1 = new Box();
//        box2 = new Box();
//        BoxInfoDTO1 = new BoxInfoDTO();
//        BoxInfoDTO2 = new BoxInfoDTO();
//        existingBox = new Box();
//        BoxInfoDTO = new BoxInfoDTO();
//        updatedBox = new Box();
//        updatedBoxInfoDTO = new BoxInfoDTO();
//    }
//
//    @Test
//    public void testGetAllBoxes() {
//        // Arrange
//        List<Box> boxes = List.of(box1, box2);
//
//        when(boxRepository.findAll()).thenReturn(boxes);
//        when(boxMapper.convertToBoxInfoDTO(box1)).thenReturn(BoxInfoDTO1);
//        when(boxMapper.convertToBoxInfoDTO(box2)).thenReturn(BoxInfoDTO2);
//
//        // Act
//        List<BoxInfoDTO> result = boxService.getAllBoxes();
//
//        // Assert
//        assertThat(result).hasSize(2);
//        assertThat(result).containsExactly(BoxInfoDTO1, BoxInfoDTO2);
//        verify(boxRepository, times(1)).findAll();
//        verify(boxMapper, times(1)).convertToBoxInfoDTO(box1);
//        verify(boxMapper, times(1)).convertToBoxInfoDTO(box2);
//    }
//
//    @Test
//    public void testGetAllBoxes_WhenBoxesIsEmpty_ShouldThrowException() {
//        // Arrange
//        when(boxRepository.findAll()).thenReturn(List.of());
//
//        // Act & Assert
//        assertThatThrownBy(() -> boxService.getAllBoxes())
//                .isInstanceOf(ResourceNotFoundException.class)
//                .hasMessageContaining("No boxes found");
//    }
//
//    @Test
//    public void testGetFirstBox() {
//        // Arrange
//        List<Box> boxes = Arrays.asList(box1);
//        when(boxRepository.findAll()).thenReturn(boxes);
//        when(boxMapper.convertToBoxInfoDTO(box1)).thenReturn(BoxInfoDTO1);
//
//        // Act
//        BoxInfoDTO result = boxService.getFirstBox();
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(result).isEqualTo(BoxInfoDTO1);
//        verify(boxRepository, times(1)).findAll();
//    }
//
//    @Test
//    public void testGetFirstBox_WhenFirstBoxNotFound_ShouldThrowException() {
//        // Arrange
//        when(boxRepository.findAll()).thenReturn(List.of());
//
//        // Act & Assert
//        assertThatThrownBy(() -> boxService.getFirstBox())
//                .isInstanceOf(ResourceNotFoundException.class)
//                .hasMessageContaining("No boxes found");
//    }
//
//    @Test
//    public void testGetBoxById() {
//        // Arrange
//        Long id = 1L;
//        when(boxRepository.findById(id)).thenReturn(Optional.of(box1));
//        when(boxMapper.convertToBoxInfoDTO(box1)).thenReturn(BoxInfoDTO1);
//
//        // Act
//        BoxInfoDTO result = boxService.getBoxById(id);
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(result).isEqualTo(BoxInfoDTO1);
//        verify(boxRepository, times(1)).findById(id);
//    }
//
//    @Test
//    public void testGetBoxById_WhenBoxNotFound_ShouldThrowException() {
//        // Arrange
//        Long id = 1L;
//        when(boxRepository.findById(id)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThatThrownBy(() -> boxService.getBoxById(id))
//                .isInstanceOf(ResourceNotFoundException.class)
//                .hasMessageContaining("Box not found with id: " + id);
//        verify(boxRepository, times(1)).findById(id);
//        verifyNoInteractions(boxMapper);
//    }
//
//    @Test
//    public void testCreateBox() {
//        // Arrange
//        when(boxMapper.convertToBoxEntity(BoxInfoDTO1, dateTimeProvider)).thenReturn(box1);
//        when(boxRepository.save(box1)).thenReturn(box1);
//        when(boxMapper.convertToBoxInfoDTO(box1)).thenReturn(BoxInfoDTO1);
//
//        // Act
//        BoxInfoDTO result = boxService.createBox(BoxInfoDTO1);
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(result).isEqualTo(BoxInfoDTO1);
//        verify(boxRepository, times(1)).save(box1);
//    }
//
//    @Test
//    public void testCreateBox_WhenFailure_ShouldThrowException() {
//        // Arrange
//        when(boxMapper.convertToBoxEntity(BoxInfoDTO1, dateTimeProvider)).thenReturn(box1);
//        when(boxRepository.save(box1)).thenThrow(new RuntimeException("Database error"));
//
//        // Act & Assert
//        assertThatThrownBy(() -> boxService.createBox(BoxInfoDTO1))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Error creating box");
//    }
//
//    @Test
//    public void testUpdateBox() {
//        // Arrange
//        Long id = 1L;
//        when(boxRepository.findById(id)).thenReturn(Optional.of(existingBox));
//        doNothing().when(boxMapper).updateBoxFromDto(BoxInfoDTO, existingBox, dateTimeProvider);
//        when(boxRepository.save(any(Box.class))).thenReturn(updatedBox);
//        when(boxMapper.convertToBoxInfoDTO(updatedBox)).thenReturn(updatedBoxInfoDTO);
//
//        // Act
//        BoxInfoDTO result = boxService.updateBox(id, BoxInfoDTO);
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(result).isEqualTo(updatedBoxInfoDTO);
//        verify(boxRepository, times(1)).findById(id);
//        verify(boxMapper, times(1)).updateBoxFromDto(BoxInfoDTO, existingBox, dateTimeProvider);
//        verify(boxRepository, times(1)).save(existingBox);
//    }
//
//    @Test
//    public void testUpdateBox_WhenBoxNotFound_ShouldThrowException() {
//        // Arrange
//        Long id = 1L;
//        when(boxRepository.findById(id)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThatThrownBy(() -> boxService.updateBox(id, BoxInfoDTO))
//                .isInstanceOf(ResourceNotFoundException.class)
//                .hasMessageContaining("Box not found with id: " + id);
//    }
//
//    @Test
//    public void testDeleteBox() {
//        // Arrange
//        Long id = 1L;
//        when(boxRepository.findById(id)).thenReturn(Optional.of(existingBox));
//        doNothing().when(boxRepository).delete(existingBox);
//
//        // Act
//        boxService.deleteBox(id);
//
//        // Assert
//        verify(boxRepository, times(1)).findById(id);
//        verify(boxRepository, times(1)).delete(existingBox);
//    }
//
//    @Test
//    public void testDeleteBox_WhenBoxNotFound_ShouldThrowException() {
//        // Arrange
//        Long id = 1L;
//        when(boxRepository.findById(id)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThatThrownBy(() -> boxService.deleteBox(id))
//                .isInstanceOf(ResourceNotFoundException.class)
//                .hasMessageContaining("Box not found with id: " + id);
//    }
}
