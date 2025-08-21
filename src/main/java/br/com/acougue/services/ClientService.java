package br.com.acougue.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.acougue.dto.ClientRequestDTO;
import br.com.acougue.dto.ClientResponseDTO;
import br.com.acougue.entities.Client;
import br.com.acougue.mapper.ClientMapper;
import br.com.acougue.repository.ClientRepository;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientMapper clientMapper;

    @Transactional
    public ClientResponseDTO create(ClientRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("DTO não pode ser nulo");
        }
        
        // Validações básicas
        if (requestDTO.getName() == null || requestDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (requestDTO.getEstablishmentId() == null) {
            throw new IllegalArgumentException("ID do estabelecimento é obrigatório");
        }

        // Verificar se já existe cliente com mesmo telefone no estabelecimento
        if (requestDTO.getNumberPhone() != null && 
            clientRepository.existsByNumberPhoneAndEstablishmentId(requestDTO.getNumberPhone(), requestDTO.getEstablishmentId())) {
            throw new IllegalArgumentException("Já existe cliente com este telefone neste estabelecimento");
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
        if (establishmentId == null) {
            throw new IllegalArgumentException("ID do estabelecimento não pode ser nulo");
        }
        
        List<Client> clients = clientRepository.findByEstablishmentId(establishmentId);
        return clientMapper.toResponseDTOList(clients);
    }

    public Optional<ClientResponseDTO> findById(Long id) {
        if (id == null) return Optional.empty();
        
        return clientRepository.findById(id)
                .map(clientMapper::toResponseDTO);
    }

    public List<ClientResponseDTO> searchByName(String name, Long establishmentId) {
        if (name == null || name.trim().isEmpty() || establishmentId == null) {
            return findByEstablishmentId(establishmentId);
        }
        
        List<Client> clients = clientRepository.findByNameContainingIgnoreCaseAndEstablishmentId(name, establishmentId);
        return clientMapper.toResponseDTOList(clients);
    }

    @Transactional
    public ClientResponseDTO update(Long id, ClientRequestDTO requestDTO) {
        if (id == null || requestDTO == null) {
            throw new IllegalArgumentException("ID e DTO não podem ser nulos");
        }

        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com id: " + id));

        clientMapper.updateEntityFromDTO(existingClient, requestDTO);
        Client updatedClient = clientRepository.save(existingClient);
        return clientMapper.toResponseDTO(updatedClient);
    }

    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        
        if (!clientRepository.existsById(id)) {
            throw new IllegalArgumentException("Cliente não encontrado com id: " + id);
        }
        
        clientRepository.deleteById(id);
    }
}