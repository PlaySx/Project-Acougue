package br.com.acougue.services;

import br.com.acougue.dto.ClientRequestDTO;
import br.com.acougue.dto.ClientResponseDTO;
import br.com.acougue.entities.Client;
import br.com.acougue.exceptions.ResourceNotFoundException;
import br.com.acougue.mapper.ClientMapper;
import br.com.acougue.repository.ClientRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    @Transactional
    public ClientResponseDTO create(ClientRequestDTO requestDTO) {
        if (requestDTO.getNumberPhone() != null && 
            clientRepository.existsByNumberPhoneAndEstablishmentId(requestDTO.getNumberPhone(), requestDTO.getEstablishmentId())) {
            throw new DataIntegrityViolationException("Já existe um cliente com este telefone neste estabelecimento.");
        }
        Client client = clientMapper.toEntity(requestDTO);
        Client savedClient = clientRepository.save(client);
        return clientMapper.toResponseDTO(savedClient);
    }

    public List<ClientResponseDTO> advancedSearch(Long establishmentId, String name, String address, String neighborhood, String productName, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;

        List<Client> clients = clientRepository.findByAdvancedFilters(establishmentId, name, address, neighborhood, productName, startDateTime, endDateTime);
        return clientMapper.toResponseDTOList(clients);
    }

    public ClientResponseDTO findById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o ID: " + id));
        return clientMapper.toResponseDTO(client);
    }

    @Transactional
    public ClientResponseDTO update(Long id, ClientRequestDTO requestDTO) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o ID: " + id));
        clientMapper.updateEntityFromDTO(existingClient, requestDTO);
        Client updatedClient = clientRepository.save(existingClient);
        return clientMapper.toResponseDTO(updatedClient);
    }

    @Transactional
    public void delete(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente não encontrado com o ID: " + id);
        }
        clientRepository.deleteById(id);
    }
}