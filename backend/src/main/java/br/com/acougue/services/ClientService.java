package br.com.acougue.services;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.acougue.dto.ClientRequestDTO;
import br.com.acougue.dto.ClientResponseDTO;
import br.com.acougue.entities.Client;
import br.com.acougue.exceptions.ResourceNotFoundException;
import br.com.acougue.mapper.ClientMapper;
import br.com.acougue.repository.ClientRepository;

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
        // Verificar se já existe cliente com mesmo telefone no estabelecimento
        if (requestDTO.getNumberPhone() != null && 
            clientRepository.existsByNumberPhoneAndEstablishmentId(requestDTO.getNumberPhone(), requestDTO.getEstablishmentId())) {
            throw new DataIntegrityViolationException("Já existe um cliente com este telefone neste estabelecimento.");
        }

        Client client = clientMapper.toEntity(requestDTO);
        Client savedClient = clientRepository.save(client);
        return clientMapper.toResponseDTO(savedClient);
    }

    public List<ClientResponseDTO> findAll() {
        List<Client> clients = clientRepository.findAll();
        return clientMapper.toResponseDTOList(clients);
    }

    public List<ClientResponseDTO> findByEstablishmentId(Long establishmentId) {
        List<Client> clients = clientRepository.findByEstablishmentId(establishmentId);
        return clientMapper.toResponseDTOList(clients);
    }

    public ClientResponseDTO findById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o ID: " + id));
        return clientMapper.toResponseDTO(client);
    }

    public List<ClientResponseDTO> searchByName(String name, Long establishmentId) {
        if (name == null || name.trim().isEmpty()) {
            return findByEstablishmentId(establishmentId);
        }
        
        List<Client> clients = clientRepository.findByNameContainingIgnoreCaseAndEstablishmentId(name, establishmentId);
        return clientMapper.toResponseDTOList(clients);
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
        Client clientToDelete = clientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o ID: " + id));
        clientRepository.delete(clientToDelete);
    }
}