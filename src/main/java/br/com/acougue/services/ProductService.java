package br.com.acougue.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.acougue.dto.ProductRequestDTO;
import br.com.acougue.dto.ProductResponseDTO;
import br.com.acougue.entities.Products;
import br.com.acougue.mapper.ProductMapper;
import br.com.acougue.repository.ProductsRepository;

@Service
public class ProductService {

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private ProductMapper productMapper;

    @Transactional
    public ProductResponseDTO create(ProductRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("DTO não pode ser nulo");
        }
        
        // Validações básicas
        if (requestDTO.getName() == null || requestDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (requestDTO.getValue() == null || requestDTO.getValue() <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }
        if (requestDTO.getEstablishmentId() == null) {
            throw new IllegalArgumentException("ID do estabelecimento é obrigatório");
        }

        Products product = productMapper.toEntity(requestDTO);
        Products savedProduct = productsRepository.save(product);
        return productMapper.toResponseDTO(savedProduct);
    }

    public List<ProductResponseDTO> findAll() {
        List<Products> products = productsRepository.findAll();
        return productMapper.toResponseDTOList(products);
    }

    public List<ProductResponseDTO> findByEstablishmentId(Long establishmentId) {
        if (establishmentId == null) {
            throw new IllegalArgumentException("ID do estabelecimento não pode ser nulo");
        }
        
        List<Products> products = productsRepository.findByEstablishmentId(establishmentId);
        return productMapper.toResponseDTOList(products);
    }

    public Optional<ProductResponseDTO> findById(Long id) {
        if (id == null) return Optional.empty();
        
        return productsRepository.findById(id)
                .map(productMapper::toResponseDTO);
    }

    public List<ProductResponseDTO> searchByName(String name, Long establishmentId) {
        if (name == null || name.trim().isEmpty() || establishmentId == null) {
            return findByEstablishmentId(establishmentId);
        }
        
        List<Products> products = productsRepository.findByNameContainingIgnoreCaseAndEstablishmentId(name, establishmentId);
        return productMapper.toResponseDTOList(products);
    }

    public List<ProductResponseDTO> findByPriceRange(Double minValue, Double maxValue, Long establishmentId) {
        if (establishmentId == null) {
            throw new IllegalArgumentException("ID do estabelecimento não pode ser nulo");
        }
        
        List<Products> products = productsRepository.findByValueBetweenAndEstablishmentId(minValue, maxValue, establishmentId);
        return productMapper.toResponseDTOList(products);
    }

    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO requestDTO) {
        if (id == null || requestDTO == null) {
            throw new IllegalArgumentException("ID e DTO não podem ser nulos");
        }

        Products existingProduct = productsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com id: " + id));

        productMapper.updateEntityFromDTO(existingProduct, requestDTO);
        Products updatedProduct = productsRepository.save(existingProduct);
        return productMapper.toResponseDTO(updatedProduct);
    }

    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        
        if (!productsRepository.existsById(id)) {
            throw new IllegalArgumentException("Produto não encontrado com id: " + id);
        }
        
        productsRepository.deleteById(id);
    }
}