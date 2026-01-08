package br.com.acougue.controller;

import br.com.acougue.dto.ProductRequestDTO;
import br.com.acougue.dto.ProductResponseDTO;
import br.com.acougue.dto.ProductSummaryDTO;
import br.com.acougue.services.ProductService;
import br.com.acougue.services.SecurityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final SecurityService securityService;

    public ProductController(ProductService productService, SecurityService securityService) {
        this.productService = productService;
        this.securityService = securityService;
    }

    @PostMapping
    @PreAuthorize("@securityService.hasAccessToEstablishment(#requestDTO.establishmentId)")
    public ResponseEntity<ProductResponseDTO> create(@RequestBody @Valid ProductRequestDTO requestDTO) {
        ProductResponseDTO responseDTO = productService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityService.canAccessProduct(#id)")
    public ResponseEntity<ProductResponseDTO> update(@PathVariable Long id, @RequestBody @Valid ProductRequestDTO requestDTO) {
        ProductResponseDTO updatedProduct = productService.update(id, requestDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.canAccessProduct(#id)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.canAccessProduct(#id)")
    public ResponseEntity<ProductResponseDTO> findById(@PathVariable Long id) {
        ProductResponseDTO product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    // NOVO ENDPOINT: Listagem leve para o dropdown de pedidos
    @GetMapping("/summary")
    @PreAuthorize("@securityService.hasAccessToEstablishment(#establishmentId)")
    public ResponseEntity<List<ProductSummaryDTO>> listSummaries(@RequestParam Long establishmentId) {
        List<ProductSummaryDTO> summaries = productService.listSummaries(establishmentId);
        return ResponseEntity.ok(summaries);
    }

    @GetMapping
    @PreAuthorize("@securityService.hasAccessToEstablishment(#establishmentId)")
    public ResponseEntity<List<ProductResponseDTO>> findAll(
            @RequestParam Long establishmentId,
            @RequestParam(required = false) String name) {
        List<ProductResponseDTO> products = productService.searchByName(name, establishmentId);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/search-price")
    @PreAuthorize("@securityService.hasAccessToEstablishment(#establishmentId)")
    public ResponseEntity<List<ProductResponseDTO>> findByPriceRange(
            @RequestParam Long establishmentId,
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        List<ProductResponseDTO> products = productService.findByPriceRange(min, max, establishmentId);
        return ResponseEntity.ok(products);
    }
}
