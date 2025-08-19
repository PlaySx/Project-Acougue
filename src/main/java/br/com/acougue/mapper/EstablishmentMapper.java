
package br.com.acougue.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.acougue.dto.EstablishmentAuthResponseDTO;
import br.com.acougue.dto.EstablishmentDTO;
import br.com.acougue.dto.EstablishmentRegisterDTO;
import br.com.acougue.entities.Establishment;

@Component
public class EstablishmentMapper {

    // ====================== MÉTODOS PARA AUTENTICAÇÃO (NOVOS) ======================
    
    /**
     * Converte EstablishmentRegisterDTO para Establishment (sem senha)
     * A senha deve ser adicionada e criptografada no Service
     */
    public Establishment toEntityFromRegisterDTO(EstablishmentRegisterDTO registerDTO) {
        if (registerDTO == null) {
            return null;
        }
        
        Establishment establishment = new Establishment();
        establishment.setUsername(registerDTO.getUsername());
        // password será setada no Service após criptografia
        establishment.setName(registerDTO.getName());
        establishment.setCnpj(registerDTO.getCnpj());
        establishment.setAddress(registerDTO.getAddress());
        
        return establishment;
    }
    
    /**
     * Converte Establishment para EstablishmentAuthResponseDTO (sem senha)
     * Para retornar após register ou login
     */
    public EstablishmentAuthResponseDTO toAuthResponseDTO(Establishment establishment) {
        if (establishment == null) {
            return null;
        }
        
        EstablishmentAuthResponseDTO dto = new EstablishmentAuthResponseDTO();
        dto.setId(establishment.getId());
        dto.setUsername(establishment.getUsername());
        dto.setName(establishment.getName());
        dto.setCnpj(establishment.getCnpj());
        dto.setAddress(establishment.getAddress());
        
        return dto;
    }

    // ====================== MÉTODOS PARA CRUD NORMAL ======================
    
    // Converte DTO para entidade (sem dados de autenticação)
    public Establishment toEntity(EstablishmentDTO dto) {
        if (dto == null) return null;
        
        Establishment establishment = new Establishment();
        establishment.setName(dto.getName());
        establishment.setCnpj(dto.getCnpj());
        establishment.setAddress(dto.getAddress());
        
        return establishment;
    }
    
    // Converte entidade para DTO
    public EstablishmentDTO toDTO(Establishment entity) {
        if (entity == null) return null;
        
        return new EstablishmentDTO(
            entity.getId(),
            entity.getName(),
            entity.getCnpj(),
            entity.getAddress(),
            entity.getClients() != null ? entity.getClients().size() : 0,
            entity.getProducts() != null ? entity.getProducts().size() : 0,
            entity.getOrders() != null ? entity.getOrders().size() : 0
        );
    }
    
    // Atualiza entidade existente com dados do DTO
    public void updateEntityFromDTO(Establishment entity, EstablishmentDTO dto) {
        if (entity == null || dto == null) return;
        
        entity.setName(dto.getName());
        entity.setCnpj(dto.getCnpj());
        entity.setAddress(dto.getAddress());
    }
    
    // Converte lista de entidades para lista de DTOs
    public List<EstablishmentDTO> toDTOList(List<Establishment> entities) {
        if (entities == null) return null;
        
        return entities.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
}
