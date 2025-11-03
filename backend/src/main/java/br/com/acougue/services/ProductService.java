package br.com.acougue.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.acougue.dto.ProductRequestDTO;
import br.com.acougue.dto.ProductResponseDTO;
import br.com.acougue.entities.Product;
import br.com.acougue.exceptions.ResourceNotFoundException;
import br.com.acougue.mapper.ProductMapper;
import br.com.acougue.repository.ProductRepository;

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

    public List<ProductResponseDTO> findAll() {
        List<Product> products = productRepository.findAll();
        return productMapper.toResponseDTOList(products);
    }

    public List<ProductResponseDTO> findByEstablishmentId(Long establishmentId) {
        List<Product> products = productRepository.findByEstablishmentId(establishmentId);
        return productMapper.toResponseDTOList(products);
    }

    public ProductResponseDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + id));
        return productMapper.toResponseDTO(product);
    }

    public List<ProductResponseDTO> searchByName(String name, Long establishmentId) {
        // Se o nome não for fornecido, retorna todos os produtos do estabelecimento.
        if (name == null || name.trim().isEmpty()) {
            return findByEstablishmentId(establishmentId);
        }
        
        List<Product> products = productRepository.findByNameContainingIgnoreCaseAndEstablishmentId(name, establishmentId);
        return productMapper.toResponseDTOList(products);
    }
    
    public List<ProductResponseDTO> findByPriceRange(Double minValue, Double maxValue, Long establishmentId) {
        List<Product> products = productRepository.findByValueBetweenAndEstablishmentId(minValue, maxValue, establishmentId);
        return productMapper.toResponseDTOList(products);
    }

    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO requestDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + id));

        productMapper.updateEntityFromDTO(existingProduct, requestDTO);
        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toResponseDTO(updatedProduct);
    }

    @Transactional
    public void delete(Long id) {
        // Busca o produto para garantir que ele existe antes de deletar.
        // Se não existir, o orElseThrow já lança a exceção correta (404).
        Product productToDelete = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + id));
        productRepository.delete(productToDelete);
    }
}