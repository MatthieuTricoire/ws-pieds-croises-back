package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.datetime.DateTimeProvider;
import com.crossfit.pieds_croises.dto.BoxDto;
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

    private Box box1;
    private Box box2;
    private BoxDto boxDto1;
    private BoxDto boxDto2;
    private Box existingBox;
    private BoxDto boxDto;
    private Box updatedBox;
    private BoxDto updatedBoxDto;

    @BeforeEach
    public void setUp() {
        box1 = new Box();
        box2 = new Box();
        boxDto1 = new BoxDto();
        boxDto2 = new BoxDto();
        existingBox = new Box();
        boxDto = new BoxDto();
        updatedBox = new Box();
        updatedBoxDto = new BoxDto();
    }

    @Test
    public void testGetAllBoxes() {
        // Arrange
        List<Box> boxes = List.of(box1, box2);

        when(boxRepository.findAll()).thenReturn(boxes);
        when(boxMapper.convertToBoxDto(box1)).thenReturn(boxDto1);
        when(boxMapper.convertToBoxDto(box2)).thenReturn(boxDto2);

        // Act
        List<BoxDto> result = boxService.getAllBoxes();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(boxDto1, boxDto2);
        verify(boxRepository, times(1)).findAll();
        verify(boxMapper, times(1)).convertToBoxDto(box1);
        verify(boxMapper, times(1)).convertToBoxDto(box2);
    }

    @Test
    public void testGetAllBoxes_WhenBoxesIsEmpty_ShouldThrowException() {
        // Arrange
        when(boxRepository.findAll()).thenReturn(List.of());

        // Act & Assert
        assertThatThrownBy(() -> boxService.getAllBoxes())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No boxes found");
    }

    @Test
    public void testGetFirstBox() {
        // Arrange
        List<Box> boxes = Arrays.asList(box1);
        when(boxRepository.findAll()).thenReturn(boxes);
        when(boxMapper.convertToBoxDto(box1)).thenReturn(boxDto1);

        // Act
        BoxDto result = boxService.getFirstBox();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(boxDto1);
        verify(boxRepository, times(1)).findAll();
    }

    @Test
    public void testGetFirstBox_WhenFirstBoxNotFound_ShouldThrowException() {
        // Arrange
        when(boxRepository.findAll()).thenReturn(List.of());

        // Act & Assert
        assertThatThrownBy(() -> boxService.getFirstBox())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No boxes found");
    }

    @Test
    public void testGetBoxById() {
        // Arrange
        Long id = 1L;
        when(boxRepository.findById(id)).thenReturn(Optional.of(box1));
        when(boxMapper.convertToBoxDto(box1)).thenReturn(boxDto1);

        // Act
        BoxDto result = boxService.getBoxById(id);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(boxDto1);
        verify(boxRepository, times(1)).findById(id);
    }

    @Test
    public void testGetBoxById_WhenBoxNotFound_ShouldThrowException() {
        // Arrange
        Long id = 1L;
        when(boxRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> boxService.getBoxById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Box not found with id: " + id);
        verify(boxRepository, times(1)).findById(id);
        verifyNoInteractions(boxMapper);
    }

    @Test
    public void testCreateBox() {
        // Arrange
        when(boxMapper.convertToBoxEntity(boxDto1, dateTimeProvider)).thenReturn(box1);
        when(boxRepository.save(box1)).thenReturn(box1);
        when(boxMapper.convertToBoxDto(box1)).thenReturn(boxDto1);

        // Act
        BoxDto result = boxService.createBox(boxDto1);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(boxDto1);
        verify(boxRepository, times(1)).save(box1);
    }

    @Test
    public void testCreateBox_WhenFailure_ShouldThrowException() {
        // Arrange
        when(boxMapper.convertToBoxEntity(boxDto1, dateTimeProvider)).thenReturn(box1);
        when(boxRepository.save(box1)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> boxService.createBox(boxDto1))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error creating box");
    }

    @Test
    public void testUpdateBox() {
        // Arrange
        Long id = 1L;
        when(boxRepository.findById(id)).thenReturn(Optional.of(existingBox));
        doNothing().when(boxMapper).updateBoxFromDto(boxDto, existingBox, dateTimeProvider);
        when(boxRepository.save(any(Box.class))).thenReturn(updatedBox);
        when(boxMapper.convertToBoxDto(updatedBox)).thenReturn(updatedBoxDto);

        // Act
        BoxDto result = boxService.updateBox(id, boxDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(updatedBoxDto);
        verify(boxRepository, times(1)).findById(id);
        verify(boxMapper, times(1)).updateBoxFromDto(boxDto, existingBox, dateTimeProvider);
        verify(boxRepository, times(1)).save(existingBox);
    }

    @Test
    public void testUpdateBox_WhenBoxNotFound_ShouldThrowException() {
        // Arrange
        Long id = 1L;
        when(boxRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> boxService.updateBox(id, boxDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Box not found with id: " + id);
    }

    @Test
    public void testDeleteBox() {
        // Arrange
        Long id = 1L;
        when(boxRepository.findById(id)).thenReturn(Optional.of(existingBox));
        doNothing().when(boxRepository).delete(existingBox);

        // Act
        boxService.deleteBox(id);

        // Assert
        verify(boxRepository, times(1)).findById(id);
        verify(boxRepository, times(1)).delete(existingBox);
    }

    @Test
    public void testDeleteBox_WhenBoxNotFound_ShouldThrowException() {
        // Arrange
        Long id = 1L;
        when(boxRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> boxService.deleteBox(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Box not found with id: " + id);
    }
}
