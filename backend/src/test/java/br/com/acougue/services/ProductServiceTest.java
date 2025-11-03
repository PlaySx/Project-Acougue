package br.com.acougue.services;

import br.com.acougue.dto.ProductRequestDTO;
import br.com.acougue.dto.ProductResponseDTO;
import br.com.acougue.entities.Establishment;
import br.com.acougue.entities.Product;
import br.com.acougue.exceptions.ResourceNotFoundException;
import br.com.acougue.mapper.ProductMapper;
import br.com.acougue.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductRequestDTO productRequestDTO;
    private ProductResponseDTO productResponseDTO;
    private Establishment establishment;

    @BeforeEach
    void setUp() {
        establishment = new Establishment();
        establishment.setId(1L);

        product = new Product();
        product.setId(1L);
        product.setName("Picanha");
        product.setValue(89.90);
        product.setEstablishment(establishment);

        productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setName("Picanha");
        productRequestDTO.setValue(89.90);
        productRequestDTO.setEstablishmentId(1L);

        productResponseDTO = new ProductResponseDTO(1L, "Picanha", "", 89.90, 1L);
    }

    @Test
    void create_shouldSaveAndReturnProductDTO() {
        // Arrange
        when(productMapper.toEntity(any(ProductRequestDTO.class))).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponseDTO(any(Product.class))).thenReturn(productResponseDTO);

        // Act
        ProductResponseDTO result = productService.create(productRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Picanha", result.getName());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void findById_shouldReturnProductDTO_whenProductExists() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponseDTO(product)).thenReturn(productResponseDTO);

        // Act
        ProductResponseDTO result = productService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void findById_shouldThrowResourceNotFoundException_whenProductDoesNotExist() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.findById(99L);
        });
    }

    @Test
    void update_shouldUpdateAndReturnDTO_whenProductExists() {
        // Arrange
        ProductRequestDTO updateDTO = new ProductRequestDTO();
        updateDTO.setName("Picanha Gold");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponseDTO(product)).thenReturn(productResponseDTO);

        // Act
        productService.update(1L, updateDTO);

        // Assert
        verify(productMapper, times(1)).updateEntityFromDTO(product, updateDTO);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void delete_shouldCallDelete_whenProductExists() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        // Act
        productService.delete(1L);

        // Assert
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void delete_shouldThrowResourceNotFoundException_whenProductDoesNotExist() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(99L);
        });

        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    void findAll_shouldReturnListOfAllProducts() {
        // Arrange
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.toResponseDTOList(anyList())).thenReturn(List.of(productResponseDTO));

        // Act
        List<ProductResponseDTO> result = productService.findAll();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void findByEstablishmentId_shouldReturnProductsForThatEstablishment() {
        // Arrange
        when(productRepository.findByEstablishmentId(1L)).thenReturn(List.of(product));
        when(productMapper.toResponseDTOList(anyList())).thenReturn(List.of(productResponseDTO));

        // Act
        List<ProductResponseDTO> result = productService.findByEstablishmentId(1L);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(productRepository, times(1)).findByEstablishmentId(1L);
    }

    @Test
    void searchByName_shouldCallFindByEstablishmentId_whenNameIsNull() {
        // Arrange
        when(productRepository.findByEstablishmentId(1L)).thenReturn(List.of(product));

        // Act
        productService.searchByName(null, 1L);

        // Assert
        verify(productRepository, times(1)).findByEstablishmentId(1L);
        verify(productRepository, never()).findByNameContainingIgnoreCaseAndEstablishmentId(anyString(), anyLong());
    }

    @Test
    void findByPriceRange_shouldReturnProductsInPriceRange() {
        // Arrange
        when(productRepository.findByValueBetweenAndEstablishmentId(50.0, 100.0, 1L)).thenReturn(List.of(product));
        when(productMapper.toResponseDTOList(anyList())).thenReturn(List.of(productResponseDTO));

        // Act
        List<ProductResponseDTO> result = productService.findByPriceRange(50.0, 100.0, 1L);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(productRepository, times(1)).findByValueBetweenAndEstablishmentId(50.0, 100.0, 1L);
    }
}