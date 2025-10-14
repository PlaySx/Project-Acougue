package br.com.acougue.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

import br.com.acougue.dto.ProductRequestDTO;
import br.com.acougue.dto.ProductResponseDTO;
import br.com.acougue.entities.Establishment;
import br.com.acougue.entities.Product;
import br.com.acougue.exceptions.ResourceNotFoundException;
import br.com.acougue.repository.EstablishmentRepository;

@Component
public class ProductMapper {

    private final EstablishmentRepository establishmentRepository;

    public ProductMapper(EstablishmentRepository establishmentRepository) {
        this.establishmentRepository = establishmentRepository;
    }
    
    // Converte DTO de request para entidade
    public Product toEntity(ProductRequestDTO dto) {
        if (dto == null) return null;
        
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setValue(dto.getValue());
        
        // Busca o estabelecimento pelo ID
        if (dto.getEstablishmentId() != null) {
            Establishment establishment = establishmentRepository.findById(dto.getEstablishmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado com o ID: " + dto.getEstablishmentId()));
            product.setEstablishment(establishment);
        }
        
        return product;
    }
    
    // Converte entidade para DTO de response
    public ProductResponseDTO toResponseDTO(Product entity) {
        if (entity == null) return null;
        
        return new ProductResponseDTO(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getValue(),
            entity.getEstablishment() != null ? entity.getEstablishment().getId() : null
        );
    }
    
    // Atualiza entidade existente com dados do DTO
    public void updateEntityFromDTO(Product entity, ProductRequestDTO dto) {
        if (entity == null || dto == null) return;
        
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setValue(dto.getValue());
        
        if (dto.getEstablishmentId() != null) {
            Establishment establishment = establishmentRepository.findById(dto.getEstablishmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado com o ID: " + dto.getEstablishmentId()));
            entity.setEstablishment(establishment);
        }
    }
    
    // Converte lista de entidades para lista de DTOs de response
    public List<ProductResponseDTO> toResponseDTOList(List<Product> entities) {
        if (entities == null) return null;
        
        return entities.stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }
}
