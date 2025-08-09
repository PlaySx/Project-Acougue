package br.com.acougue.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.acougue.dto.ProductRequestDTO;
import br.com.acougue.dto.ProductResponseDTO;
import br.com.acougue.entities.Products;
import br.com.acougue.globalException.ProductNaoEncontradoException;
import br.com.acougue.mapper.ProductMapper;
import br.com.acougue.repository.ProductsRepository;

@Service
public class ProductService {
	
	@Autowired
	private ProductsRepository productRepository;
	
	@Autowired
	private ProductMapper productMapper;
	
	@Transactional
    public ProductResponseDTO create(ProductRequestDTO productRequestDTO) {
        Products product = productMapper.toEntity(productRequestDTO);
        Products savedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(savedProduct);
    }

    public List<ProductResponseDTO> findAll() {
        List<Products> products = productRepository.findAll();
        return productMapper.toResponseDTOList(products);
    }

    public Optional<ProductResponseDTO> findById(Long id) {
        return productRepository.findById(id)
        		.map(productMapper::toResponseDTO);
    }

    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO productRequestDTO) {
        Products existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNaoEncontradoException("Produto não encontrado com id: " + id));
        
        productMapper.updateEntityFromDTO(existingProduct, productRequestDTO);
        Products updatedProduct = productRepository.save(existingProduct);
        return productMapper.toResponseDTO(updatedProduct);
    }

    @Transactional
    public void delete(Long id) {
    	if (!productRepository.existsById(id)) {
            throw new ProductNaoEncontradoException("Produto não encontrado com id: " + id);
        }
        productRepository.deleteById(id);
    }
}