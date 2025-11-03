package br.com.acougue.mapper;

import br.com.acougue.dto.ProductRequestDTO;
import br.com.acougue.dto.ProductResponseDTO;
import br.com.acougue.entities.Establishment;
import br.com.acougue.entities.Product;
import br.com.acougue.exceptions.ResourceNotFoundException;
import br.com.acougue.repository.EstablishmentRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductMapperTest {

    @Mock
    private EstablishmentRepository establishmentRepository;

    @InjectMocks
    private ProductMapper productMapper;

    private ProductRequestDTO productRequestDTO;
    private Establishment establishment;

    @BeforeEach
    void setUp() {
        establishment = new Establishment();
        establishment.setId(1L);
        establishment.setName("Açougue Central");

        productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setName("Picanha");
        productRequestDTO.setValue(90.0);
        productRequestDTO.setEstablishmentId(1L);
    }

    @Test
    void toEntity_shouldReturnProductWithEstablishment_whenEstablishmentExists() {
        // Arrange
        when(establishmentRepository.findById(1L)).thenReturn(Optional.of(establishment));

        // Act
        Product result = productMapper.toEntity(productRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Picanha", result.getName());
        assertNotNull(result.getEstablishment());
        assertEquals(1L, result.getEstablishment().getId());
        verify(establishmentRepository, times(1)).findById(1L);
    }

    @Test
    void toEntity_shouldThrowResourceNotFoundException_whenEstablishmentDoesNotExist() {
        // Arrange
        when(establishmentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productMapper.toEntity(productRequestDTO);
        });
    }

    @Test
    void updateEntityFromDTO_shouldUpdateProductCorrectly() {
        // Arrange
        Product existingProduct = new Product();
        when(establishmentRepository.findById(1L)).thenReturn(Optional.of(establishment));

        // Act
        productMapper.updateEntityFromDTO(existingProduct, productRequestDTO);

        // Assert
        assertEquals("Picanha", existingProduct.getName());
        assertEquals(90.0, existingProduct.getValue());
        assertEquals(establishment, existingProduct.getEstablishment());
    }

    @Test
    void updateEntityFromDTO_shouldThrowResourceNotFoundException_whenEstablishmentDoesNotExist() {
        // Arrange
        Product existingProduct = new Product();
        when(establishmentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productMapper.updateEntityFromDTO(existingProduct, productRequestDTO);
        });
    }

    @Test
    void toResponseDTO_shouldConvertEntityToResponseDTO() {
        // Arrange
        Product product = new Product();
        product.setId(1L);
        product.setName("Picanha");
        product.setValue(90.0);
        product.setEstablishment(establishment);

        // Act
        ProductResponseDTO responseDTO = productMapper.toResponseDTO(product);

        // Assert
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals("Picanha", responseDTO.getName());
        assertEquals(90.0, responseDTO.getValue());
        assertEquals(1L, responseDTO.getEstablishmentId());
    }

    @Test
    void toResponseDTOList_shouldConvertListOfEntities() {
        // Arrange
        Product product = new Product();
        product.setId(1L);
        product.setName("Picanha");
        product.setValue(90.0);
        product.setEstablishment(establishment);
        List<Product> productList = Collections.singletonList(product);

        // Act
        List<ProductResponseDTO> dtoList = productMapper.toResponseDTOList(productList);

        // Assert
        assertNotNull(dtoList);
        assertFalse(dtoList.isEmpty());
        ProductResponseDTO resultDTO = dtoList.get(0);
        assertEquals(1L, resultDTO.getId());
        assertEquals("Picanha", resultDTO.getName());
    }
}