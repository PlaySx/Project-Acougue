package br.com.acougue.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

import br.com.acougue.dto.ClientRequestDTO;
import br.com.acougue.dto.ClientResponseDTO;
import br.com.acougue.entities.Client;
import br.com.acougue.entities.Establishment;
import br.com.acougue.exceptions.ResourceNotFoundException;
import br.com.acougue.repository.EstablishmentRepository;

@Component
public class ClientMapper {
    private final EstablishmentRepository establishmentRepository;

    public ClientMapper(EstablishmentRepository establishmentRepository) {
        this.establishmentRepository = establishmentRepository;
    }
    
    // Converte DTO de request para entidade
    public Client toEntity(ClientRequestDTO dto) {
        if (dto == null) return null;
        
        Client client = new Client();
        client.setName(dto.getName());
        client.setNumberPhone(dto.getNumberPhone());
        client.setAddress(dto.getAddress());
        client.setAddressNeighborhood(dto.getAddressNeighborhood());
        client.setObservation(dto.getObservation());
        
        // Busca o estabelecimento pelo ID
        if (dto.getEstablishmentId() != null) {
            Establishment establishment = establishmentRepository.findById(dto.getEstablishmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado com o ID: " + dto.getEstablishmentId()));
            client.setEstablishment(establishment);
        }
        
        return client;
    }
    
    // Converte entidade para DTO de response
    public ClientResponseDTO toResponseDTO(Client entity) {
        if (entity == null) return null;
        
        return new ClientResponseDTO(
            entity.getId(),
            entity.getName(),
            entity.getNumberPhone(),
            entity.getAddress(),
            entity.getAddressNeighborhood(),
            entity.getObservation(),
            entity.getEstablishment() != null ? entity.getEstablishment().getId() : null,
            entity.getOrders() != null ? entity.getOrders().size() : 0
        );
    }
    
    // Atualiza entidade existente com dados do DTO
    public void updateEntityFromDTO(Client entity, ClientRequestDTO dto) {
        if (entity == null || dto == null) return;
        
        entity.setName(dto.getName());
        entity.setNumberPhone(dto.getNumberPhone());
        entity.setAddress(dto.getAddress());
        entity.setAddressNeighborhood(dto.getAddressNeighborhood());
        entity.setObservation(dto.getObservation());
        
        if (dto.getEstablishmentId() != null) {
            Establishment establishment = establishmentRepository.findById(dto.getEstablishmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado com o ID: " + dto.getEstablishmentId()));
            entity.setEstablishment(establishment);
        }
    }
    
    // Converte lista de entidades para lista de DTOs de response
    public List<ClientResponseDTO> toResponseDTOList(List<Client> entities) {
        if (entities == null) return null;
        
        return entities.stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }
}
