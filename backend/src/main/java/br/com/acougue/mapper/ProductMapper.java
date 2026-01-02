package br.com.acougue.mapper;

import br.com.acougue.dto.ProductRequestDTO;
import br.com.acougue.dto.ProductResponseDTO;
import br.com.acougue.entities.Establishment;
import br.com.acougue.entities.Product;
import br.com.acougue.exceptions.ResourceNotFoundException;
import br.com.acougue.repository.EstablishmentRepository;
import org.owasp.html.PolicyFactory; // 1. Importa
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    private final EstablishmentRepository establishmentRepository;
    private final PolicyFactory htmlPolicy; // 2. Injeta a política de sanitização

    public ProductMapper(EstablishmentRepository establishmentRepository, PolicyFactory htmlPolicy) {
        this.establishmentRepository = establishmentRepository;
        this.htmlPolicy = htmlPolicy;
    }

    public Product toEntity(ProductRequestDTO dto) {
        if (dto == null) return null;

        Product product = new Product();
        
        // 3. Sanitiza os campos de texto antes de colocar na entidade
        product.setName(htmlPolicy.sanitize(dto.getName()));
        product.setDescription(htmlPolicy.sanitize(dto.getDescription()));
        
        product.setCategory(dto.getCategory());
        product.setPricingType(dto.getPricingType());
        product.setUnitPrice(dto.getUnitPrice());
        product.setStockQuantity(dto.getStockQuantity());

        Establishment establishment = establishmentRepository.findById(dto.getEstablishmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado com o ID: " + dto.getEstablishmentId()));
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
                entity.getStockQuantity(),
                entity.getEstablishment() != null ? entity.getEstablishment().getId() : null
        );
    }

    public void updateEntityFromDTO(Product entity, ProductRequestDTO dto) {
        if (entity == null || dto == null) return;

        // 3. Sanitiza também na atualização
        entity.setName(htmlPolicy.sanitize(dto.getName()));
        entity.setDescription(htmlPolicy.sanitize(dto.getDescription()));
        entity.setCategory(dto.getCategory());
        entity.setPricingType(dto.getPricingType());
        entity.setUnitPrice(dto.getUnitPrice());
        entity.setStockQuantity(dto.getStockQuantity());
    }

    public List<ProductResponseDTO> toResponseDTOList(List<Product> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }
}
