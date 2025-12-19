package br.com.acougue.mapper;

import br.com.acougue.dto.ProductRequestDTO;
import br.com.acougue.dto.ProductResponseDTO;
import br.com.acougue.entities.Establishment;
import br.com.acougue.entities.Product;
import br.com.acougue.repository.EstablishmentRepository;
import br.com.acougue.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    private final EstablishmentRepository establishmentRepository;
    
    public ProductMapper(EstablishmentRepository establishmentRepository) {
        this.establishmentRepository = establishmentRepository;
    }
    
    public Product toEntity(ProductRequestDTO dto) {
        if (dto == null) return null;
        
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setCategory(dto.getCategory());
        product.setPricingType(dto.getPricingType());
        product.setUnitPrice(dto.getUnitPrice());
        
        Establishment establishment = establishmentRepository.findById(dto.getEstablishmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado com id: " + dto.getEstablishmentId()));
        product.setEstablishment(establishment);
        
        return product;
    }
    
    public ProductResponseDTO toResponseDTO(Product entity) {
        if (entity == null) return null;
        
        return new ProductResponseDTO(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getCategory(),
            entity.getPricingType(),
            entity.getUnitPrice(),
            entity.getEstablishment() != null ? entity.getEstablishment().getId() : null
        );
    }
    
    public void updateEntityFromDTO(Product entity, ProductRequestDTO dto) {
        if (entity == null || dto == null) return;
        
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCategory(dto.getCategory());
        entity.setPricingType(dto.getPricingType());
        entity.setUnitPrice(dto.getUnitPrice());
    }
    
    public List<ProductResponseDTO> toResponseDTOList(List<Product> entities) {
        if (entities == null) return null;
        
        return entities.stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }
}