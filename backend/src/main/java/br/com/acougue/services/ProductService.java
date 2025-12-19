package br.com.acougue.services;

import br.com.acougue.dto.ProductRequestDTO;
import br.com.acougue.dto.ProductResponseDTO;
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

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional
    public ProductResponseDTO create(ProductRequestDTO requestDTO) {
        Product product = productMapper.toEntity(requestDTO);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + id));
        return productMapper.toResponseDTO(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> searchByName(String name, Long establishmentId) {
        if (name == null || name.trim().isEmpty()) {
            return findByEstablishmentId(establishmentId);
        }
        List<Product> products = productRepository.findByNameContainingIgnoreCaseAndEstablishmentId(name, establishmentId);
        return productMapper.toResponseDTOList(products);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findByEstablishmentId(Long establishmentId) {
        List<Product> products = productRepository.findByEstablishmentId(establishmentId);
        return productMapper.toResponseDTOList(products);
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findByPriceRange(BigDecimal minValue, BigDecimal maxValue, Long establishmentId) {
        // CORREÇÃO FINAL: Usando o nome de método  correto do repositório
        List<Product> products = productRepository.findByUnitPriceBetweenAndEstablishmentId(minValue, maxValue, establishmentId);
        return productMapper.toResponseDTOList(products);
    }

    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO requestDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + id));

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isOwner = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.ROLE_OWNER.name()));

        if (!isOwner) {
            if (existingProduct.getUnitPrice().compareTo(requestDTO.getUnitPrice()) != 0) {
                throw new AccessDeniedException("Funcionários não podem alterar o preço dos produtos.");
            }
        }

        productMapper.updateEntityFromDTO(existingProduct, requestDTO);
        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toResponseDTO(updatedProduct);
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produto não encontrado com o ID: " + id);
        }
        productRepository.deleteById(id);
    }
}
