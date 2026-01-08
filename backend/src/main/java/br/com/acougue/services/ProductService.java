package br.com.acougue.services;

import br.com.acougue.dto.ProductRequestDTO;
import br.com.acougue.dto.ProductResponseDTO;
import br.com.acougue.dto.ProductSummaryDTO;
import br.com.acougue.entities.Product;
import br.com.acougue.enums.Role;
import br.com.acougue.exceptions.ResourceNotFoundException;
import br.com.acougue.mapper.ProductMapper;
import br.com.acougue.repository.ProductRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final AuditService auditService;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper, AuditService auditService) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.auditService = auditService;
    }

    // NOVO MÉTODO: Listagem leve
    @Transactional(readOnly = true)
    public List<ProductSummaryDTO> listSummaries(Long establishmentId) {
        return productRepository.findProductSummariesByEstablishmentId(establishmentId);
    }

    @Transactional
    public ProductResponseDTO create(ProductRequestDTO requestDTO) {
        Product product = productMapper.toEntity(requestDTO);
        Product savedProduct = productRepository.save(product);
        auditService.logAction("CREATE", "Product", savedProduct.getId().toString(), "Produto criado: " + savedProduct.getName(), savedProduct.getEstablishment().getId());
        return productMapper.toResponseDTO(savedProduct);
    }

    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO requestDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + id));

        String oldDetails = "Preço: " + existingProduct.getUnitPrice() + ", Estoque: " + existingProduct.getStockQuantity();

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isOwner = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.ROLE_OWNER.name()));

        if (!isOwner && existingProduct.getUnitPrice().compareTo(requestDTO.getUnitPrice()) != 0) {
            throw new AccessDeniedException("Funcionários não podem alterar o preço dos produtos.");
        }

        productMapper.updateEntityFromDTO(existingProduct, requestDTO);
        Product updatedProduct = productRepository.save(existingProduct);

        String newDetails = "Preço: " + updatedProduct.getUnitPrice() + ", Estoque: " + updatedProduct.getStockQuantity();
        auditService.logAction("UPDATE", "Product", updatedProduct.getId().toString(), "De: [" + oldDetails + "] Para: [" + newDetails + "]", updatedProduct.getEstablishment().getId());

        return productMapper.toResponseDTO(updatedProduct);
    }

    @Transactional
    public void delete(Long id) {
        Product productToDelete = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + id));
        
        productRepository.delete(productToDelete);
        
        auditService.logAction("DELETE", "Product", id.toString(), "Produto deletado: " + productToDelete.getName(), productToDelete.getEstablishment().getId());
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + id));
        return productMapper.toResponseDTO(product);
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> searchByName(String name, Long establishmentId) {
        if (name == null || name.trim().isEmpty()) {
            return findByEstablishmentId(establishmentId);
        }
        return productMapper.toResponseDTOList(productRepository.findByNameContainingIgnoreCaseAndEstablishmentId(name, establishmentId));
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findByEstablishmentId(Long establishmentId) {
        return productMapper.toResponseDTOList(productRepository.findByEstablishmentId(establishmentId));
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findByPriceRange(BigDecimal minValue, BigDecimal maxValue, Long establishmentId) {
        List<Product> products = productRepository.findByUnitPriceBetweenAndEstablishmentId(minValue, maxValue, establishmentId);
        return productMapper.toResponseDTOList(products);
    }
}
