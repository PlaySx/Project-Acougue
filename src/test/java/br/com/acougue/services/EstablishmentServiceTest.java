package br.com.acougue.services;

import br.com.acougue.dto.EstablishmentDTO;
import br.com.acougue.entities.Establishment;
import br.com.acougue.exceptions.ResourceNotFoundException;
import br.com.acougue.mapper.EstablishmentMapper;
import br.com.acougue.repository.EstablishmentRepository;
import br.com.acougue.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstablishmentServiceTest {

    @Mock
    private EstablishmentRepository establishmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EstablishmentMapper establishmentMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EstablishmentService establishmentService;

    private Establishment establishment;
    private EstablishmentDTO establishmentDTO;

    @BeforeEach
    void setUp() {
        establishment = new Establishment();
        establishment.setId(1L);
        establishment.setName("Açougue Central");

        establishmentDTO = new EstablishmentDTO();
        establishmentDTO.setId(1L);
        establishmentDTO.setName("Açougue Central");
    }

    @Test
    void create_shouldSaveAndReturnDTO() {
        // Arrange
        when(establishmentMapper.toEntity(any(EstablishmentDTO.class))).thenReturn(establishment);
        when(establishmentRepository.save(any(Establishment.class))).thenReturn(establishment);
        when(establishmentMapper.toDTO(any(Establishment.class))).thenReturn(establishmentDTO);

        // Act
        EstablishmentDTO result = establishmentService.create(establishmentDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Açougue Central", result.getName());
        verify(establishmentRepository, times(1)).save(establishment);
    }

    @Test
    void findById_shouldReturnDTO_whenEstablishmentExists() {
        // Arrange
        when(establishmentRepository.findById(1L)).thenReturn(Optional.of(establishment));
        when(establishmentMapper.toDTO(establishment)).thenReturn(establishmentDTO);

        // Act
        EstablishmentDTO result = establishmentService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(establishmentRepository, times(1)).findById(1L);
    }

    @Test
    void findById_shouldThrowResourceNotFoundException_whenEstablishmentDoesNotExist() {
        // Arrange
        when(establishmentRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            establishmentService.findById(99L);
        });
    }

    @Test
    void delete_shouldCallDelete_whenEstablishmentExists() {
        // Arrange
        when(establishmentRepository.findById(1L)).thenReturn(Optional.of(establishment));
        doNothing().when(establishmentRepository).delete(establishment);

        // Act
        establishmentService.delete(1L);

        // Assert
        verify(establishmentRepository, times(1)).delete(establishment);
    }
}