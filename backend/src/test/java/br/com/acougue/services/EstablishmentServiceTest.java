package br.com.acougue.services;

import br.com.acougue.dto.EstablishmentCreateRequestDTO;
import br.com.acougue.dto.EstablishmentDTO;
import br.com.acougue.entities.Establishment;
import br.com.acougue.entities.User;
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
import org.springframework.dao.DataIntegrityViolationException;
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
    private User user;
    private EstablishmentCreateRequestDTO createRequestDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        establishment = new Establishment();
        establishment.setId(1L);
        establishment.setName("Açougue Central");
        establishment.setCnpj(12345678901234L);

        establishmentDTO = new EstablishmentDTO();
        establishmentDTO.setId(1L);
        establishmentDTO.setName("Açougue Central");

        createRequestDTO = new EstablishmentCreateRequestDTO();
        createRequestDTO.setName("Novo Açougue");
        createRequestDTO.setCnpj(98765432109876L);
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
        verify(establishmentRepository).findById(1L);
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
    void createForUser_shouldCreateEstablishmentAndAssociateWithUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(establishmentRepository.existsByCnpj(any(Long.class))).thenReturn(false);
        when(establishmentMapper.toDTO(any(Establishment.class))).thenReturn(establishmentDTO);

        // Act
        EstablishmentDTO result = establishmentService.createForUser(1L, createRequestDTO);

        // Assert
        assertNotNull(result);
        verify(userRepository).save(user); // Verifica se o usuário com o novo estabelecimento foi salvo
        assertNotNull(user.getEstablishment());
        assertEquals("Novo Açougue", user.getEstablishment().getName());
    }

    @Test
    void createForUser_shouldThrowDataIntegrityViolationException_whenCnpjExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(establishmentRepository.existsByCnpj(any(Long.class))).thenReturn(true);

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            establishmentService.createForUser(1L, createRequestDTO);
        });

        verify(userRepository, never()).save(any(User.class));
    }
}