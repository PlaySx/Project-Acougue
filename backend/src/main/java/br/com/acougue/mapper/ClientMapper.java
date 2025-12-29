package br.com.acougue.mapper;

import br.com.acougue.dto.ClientRequestDTO;
import br.com.acougue.dto.ClientResponseDTO;
import br.com.acougue.dto.PhoneNumberDTO;
import br.com.acougue.entities.Client;
import br.com.acougue.entities.Establishment;
import br.com.acougue.entities.PhoneNumber;
import br.com.acougue.exceptions.ResourceNotFoundException;
import br.com.acougue.repository.EstablishmentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClientMapper {
    private final EstablishmentRepository establishmentRepository;

    public ClientMapper(EstablishmentRepository establishmentRepository) {
        this.establishmentRepository = establishmentRepository;
    }
    
    public Client toEntity(ClientRequestDTO dto) {
        if (dto == null) return null;
        
        Client client = new Client();
        client.setName(dto.getName());
        client.setAddress(dto.getAddress());
        client.setAddressNeighborhood(dto.getAddressNeighborhood());
        client.setObservation(dto.getObservation());
        
        dto.getPhoneNumbers().forEach(phoneDto -> {
            PhoneNumber phone = new PhoneNumber();
            phone.setType(phoneDto.getType());
            phone.setNumber(phoneDto.getNumber());
            phone.setPrimary(phoneDto.isPrimary());
            client.addPhoneNumber(phone);
        });
        
        Establishment establishment = establishmentRepository.findById(dto.getEstablishmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado com o ID: " + dto.getEstablishmentId()));
        client.setEstablishment(establishment);
        
        return client;
    }
    
    public ClientResponseDTO toResponseDTO(Client entity) {
        if (entity == null) return null;
        
        List<PhoneNumberDTO> phoneDTOs = entity.getPhoneNumbers().stream()
            .map(phone -> new PhoneNumberDTO(phone.getType(), phone.getNumber(), phone.isPrimary()))
            .collect(Collectors.toList());

        return new ClientResponseDTO(
            entity.getId(),
            entity.getName(),
            phoneDTOs,
            entity.getAddress(),
            entity.getAddressNeighborhood(),
            entity.getObservation(),
            entity.getEstablishment() != null ? entity.getEstablishment().getId() : null,
            entity.getOrders() != null ? entity.getOrders().size() : 0
        );
    }
    
    public void updateEntityFromDTO(Client entity, ClientRequestDTO dto) {
        if (entity == null || dto == null) return;
        
        entity.setName(dto.getName());
        entity.setAddress(dto.getAddress());
        entity.setAddressNeighborhood(dto.getAddressNeighborhood());
        entity.setObservation(dto.getObservation());

        // Lógica complexa de atualização de telefones (simplificada aqui)
        // A abordagem mais segura seria limpar e adicionar os novos.
        entity.getPhoneNumbers().clear();
        dto.getPhoneNumbers().forEach(phoneDto -> {
            PhoneNumber phone = new PhoneNumber();
            phone.setType(phoneDto.getType());
            phone.setNumber(phoneDto.getNumber());
            phone.setPrimary(phoneDto.isPrimary());
            entity.addPhoneNumber(phone);
        });
    }
    
    public List<ClientResponseDTO> toResponseDTOList(List<Client> entities) {
        if (entities == null) return null;
        
        return entities.stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }
}
