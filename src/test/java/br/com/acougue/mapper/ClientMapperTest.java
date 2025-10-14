package br.com.acougue.mapper;

import br.com.acougue.dto.ClientRequestDTO;
import br.com.acougue.dto.ClientResponseDTO;
import br.com.acougue.entities.Client;
import br.com.acougue.entities.Establishment;
import br.com.acougue.exceptions.ResourceNotFoundException;
import br.com.acougue.repository.EstablishmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientMapperTest {

    @Mock
    private EstablishmentRepository establishmentRepository;

    @InjectMocks
    private ClientMapper clientMapper;

    private ClientRequestDTO clientRequestDTO;
    private Establishment establishment;

    @BeforeEach
    void setUp() {
        establishment = new Establishment();
        establishment.setId(1L);

        clientRequestDTO = new ClientRequestDTO();
        clientRequestDTO.setName("João Silva");
        clientRequestDTO.setNumberPhone(999887766L);
        clientRequestDTO.setEstablishmentId(1L);
    }

    @Test
    void toEntity_shouldReturnClientWithEstablishment_whenEstablishmentExists() {
        // Arrange
        when(establishmentRepository.findById(1L)).thenReturn(Optional.of(establishment));

        // Act
        Client result = clientMapper.toEntity(clientRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("João Silva", result.getName());
        assertNotNull(result.getEstablishment());
        assertEquals(1L, result.getEstablishment().getId());
        verify(establishmentRepository, times(1)).findById(1L);
    }

    @Test
    void toEntity_shouldThrowResourceNotFoundException_whenEstablishmentDoesNotExist() {
        // Arrange
        when(establishmentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            clientMapper.toEntity(clientRequestDTO);
        });
    }

    @Test
    void updateEntityFromDTO_shouldUpdateClientCorrectly() {
        // Arrange
        Client existingClient = new Client();
        when(establishmentRepository.findById(1L)).thenReturn(Optional.of(establishment));

        // Act
        clientMapper.updateEntityFromDTO(existingClient, clientRequestDTO);

        // Assert
        assertEquals("João Silva", existingClient.getName());
        assertEquals(999887766L, existingClient.getNumberPhone());
        assertEquals(establishment, existingClient.getEstablishment());
    }

    @Test
    void updateEntityFromDTO_shouldThrowResourceNotFoundException_whenEstablishmentDoesNotExist() {
        // Arrange
        Client existingClient = new Client();
        when(establishmentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            clientMapper.updateEntityFromDTO(existingClient, clientRequestDTO);
        });
    }

    @Test
    void toResponseDTO_shouldConvertEntityToResponseDTO() {
        // Arrange
        Client client = new Client();
        client.setId(1L);
        client.setName("João Silva");
        client.setEstablishment(establishment);

        // Act
        ClientResponseDTO responseDTO = clientMapper.toResponseDTO(client);

        // Assert
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals("João Silva", responseDTO.getName());
        assertEquals(1L, responseDTO.getEstablishmentId());
    }

    @Test
    void toResponseDTOList_shouldConvertListOfEntities() {
        // Arrange
        Client client = new Client();
        client.setId(1L);
        List<Client> clientList = Collections.singletonList(client);

        // Act
        List<ClientResponseDTO> dtoList = clientMapper.toResponseDTOList(clientList);

        // Assert
        assertNotNull(dtoList);
        assertEquals(1, dtoList.size());
    }
}