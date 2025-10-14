package br.com.acougue.services;

import br.com.acougue.dto.ClientRequestDTO;
import br.com.acougue.dto.ClientResponseDTO;
import br.com.acougue.entities.Client;
import br.com.acougue.entities.Establishment;
import br.com.acougue.exceptions.ResourceNotFoundException;
import br.com.acougue.mapper.ClientMapper;
import br.com.acougue.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientService clientService;

    private Client client;
    private ClientRequestDTO clientRequestDTO;
    private ClientResponseDTO clientResponseDTO;
    private Establishment establishment;

    @BeforeEach
    void setUp() {
        establishment = new Establishment();
        establishment.setId(1L);

        client = new Client();
        client.setId(1L);
        client.setName("João Silva");
        client.setNumberPhone(999998888L);
        client.setEstablishment(establishment);

        clientRequestDTO = new ClientRequestDTO();
        clientRequestDTO.setName("João Silva");
        clientRequestDTO.setNumberPhone(999998888L);
        clientRequestDTO.setEstablishmentId(1L);

        clientResponseDTO = new ClientResponseDTO();
        clientResponseDTO.setId(1L);
        clientResponseDTO.setName("João Silva");
    }

    @Test
    void create_shouldSaveAndReturnClientDTO_whenDataIsValid() {
        // Arrange
        when(clientRepository.existsByNumberPhoneAndEstablishmentId(anyLong(), anyLong())).thenReturn(false);
        when(clientMapper.toEntity(any(ClientRequestDTO.class))).thenReturn(client);
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(clientMapper.toResponseDTO(any(Client.class))).thenReturn(clientResponseDTO);

        // Act
        ClientResponseDTO result = clientService.create(clientRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("João Silva", result.getName());
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    void create_shouldThrowDataIntegrityViolationException_whenPhoneNumberExists() {
        // Arrange
        when(clientRepository.existsByNumberPhoneAndEstablishmentId(anyLong(), anyLong())).thenReturn(true);

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            clientService.create(clientRequestDTO);
        });

        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void findById_shouldReturnClientDTO_whenClientExists() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientMapper.toResponseDTO(client)).thenReturn(clientResponseDTO);

        // Act
        ClientResponseDTO result = clientService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(clientRepository, times(1)).findById(1L);
    }

    @Test
    void findById_shouldThrowResourceNotFoundException_whenClientDoesNotExist() {
        // Arrange
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            clientService.findById(99L);
        });
    }

    @Test
    void delete_shouldCallDelete_whenClientExists() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        doNothing().when(clientRepository).delete(client);

        // Act
        clientService.delete(1L);

        // Assert
        verify(clientRepository, times(1)).delete(client);
    }

    @Test
    void delete_shouldThrowResourceNotFoundException_whenClientDoesNotExist() {
        // Arrange
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            clientService.delete(99L);
        });

        verify(clientRepository, never()).delete(any(Client.class));
    }

    @Test
    void update_shouldUpdateAndReturnDTO_whenClientExists() {
        // Arrange
        ClientRequestDTO updateDTO = new ClientRequestDTO();
        updateDTO.setName("João da Silva Sauro");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(clientMapper.toResponseDTO(client)).thenReturn(clientResponseDTO);

        // Act
        clientService.update(1L, updateDTO);

        // Assert
        verify(clientMapper, times(1)).updateEntityFromDTO(client, updateDTO);
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    void update_shouldThrowResourceNotFoundException_whenClientDoesNotExist() {
        // Arrange
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            clientService.update(99L, new ClientRequestDTO());
        });

        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void findAll_shouldReturnListOfClientDTOs() {
        // Arrange
        when(clientRepository.findAll()).thenReturn(List.of(client));
        when(clientMapper.toResponseDTOList(anyList())).thenReturn(List.of(clientResponseDTO));

        // Act
        List<ClientResponseDTO> result = clientService.findAll();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    void searchByName_shouldCallFindByEstablishmentId_whenNameIsNull() {
        // Arrange
        when(clientRepository.findByEstablishmentId(1L)).thenReturn(List.of(client));

        // Act
        clientService.searchByName(null, 1L);

        // Assert
        verify(clientRepository, times(1)).findByEstablishmentId(1L);
        verify(clientRepository, never()).findByNameContainingIgnoreCaseAndEstablishmentId(anyString(), anyLong());
    }
}