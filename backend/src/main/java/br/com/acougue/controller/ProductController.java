package br.com.acougue.controller;

import br.com.acougue.dto.ProductRequestDTO;
import br.com.acougue.dto.ProductResponseDTO;
import br.com.acougue.services.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/products")
@Validated
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO requestDTO) {
        ProductResponseDTO newProduct = productService.create(requestDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newProduct.getId()).toUri();
        return ResponseEntity.created(uri).body(newProduct);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> findById(@PathVariable Long id) {
        ProductResponseDTO product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> search(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "establishmentId") @NotNull(message = "O ID do estabelecimento é obrigatório.") @Min(value = 1, message = "ID do estabelecimento inválido.") Long establishmentId
    ) {
        List<ProductResponseDTO> products = productService.searchByName(name, establishmentId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<ProductResponseDTO>> findByPriceRange(
            @RequestParam(name = "min") BigDecimal minValue, // Corrigido para BigDecimal
            @RequestParam(name = "max") BigDecimal maxValue, // Corrigido para BigDecimal
            @RequestParam(name = "establishmentId") @NotNull(message = "O ID do estabelecimento é obrigatório.") @Min(value = 1, message = "ID do estabelecimento inválido.") Long establishmentId
    ) {
        List<ProductResponseDTO> products = productService.findByPriceRange(minValue, maxValue, establishmentId);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO requestDTO) {
        ProductResponseDTO updatedProduct = productService.update(id, requestDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}