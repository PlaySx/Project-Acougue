package br.com.acougue.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.acougue.dto.ClientRequestDTO;
import br.com.acougue.dto.ClientResponseDTO;
import br.com.acougue.entities.Client;
import br.com.acougue.globalException.ClienteNaoEncontradoException;
import br.com.acougue.mapper.ClientMapper;
import br.com.acougue.repository.ClientRepository;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private ClientMapper clientMapper;

    @Transactional
    public ClientResponseDTO create(ClientRequestDTO clientRequestDTO) {
        Client client = clientMapper.toEntity(clientRequestDTO);
        Client savedClient = clientRepository.save(client);
        
        // Buscar novamente para garantir que vem com todos os dados carregados
        Client clientWithRelations = clientRepository.findById(savedClient.getId())
                .orElseThrow(() -> new RuntimeException("Erro ao buscar cliente salvo"));
        
        return clientMapper.toResponseDTO(clientWithRelations);
    }

    public List<ClientResponseDTO> findAll() {
        List<Client> clients = clientRepository.findAll();
        return clientMapper.toResponseDTOList(clients);
    }

    public Optional<ClientResponseDTO> findById(Long id) {
        return clientRepository.findById(id)
        		.map(clientMapper::toResponseDTO);
    }

    @Transactional
    public ClientResponseDTO update(Long id, ClientRequestDTO clientRequestDTO) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado com id: " + id));
        
        clientMapper.updateEntityFromDTO(existingClient, clientRequestDTO);
        Client updatedClient = clientRepository.save(existingClient);
        return clientMapper.toResponseDTO(updatedClient);
    }

    @Transactional
    public void delete(Long id) {
    	if (!clientRepository.existsById(id)) {
            throw new ClienteNaoEncontradoException("Cliente não encontrado com id: " + id);
        }
        clientRepository.deleteById(id);
    }
}