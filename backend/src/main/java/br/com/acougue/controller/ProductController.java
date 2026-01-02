package br.com.acougue.controller;

import br.com.acougue.dto.ProductRequestDTO;
import br.com.acougue.dto.ProductResponseDTO;
import br.com.acougue.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    @PreAuthorize("@securityService.hasAccessToEstablishment(#productRequestDTO.establishmentId)")
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO created = productService.create(productRequestDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @GetMapping
    @PreAuthorize("@securityService.hasAccessToEstablishment(#establishmentId)")
    public ResponseEntity<List<ProductResponseDTO>> find(
            @RequestParam(name = "establishmentId") Long establishmentId,
            @RequestParam(name = "name", required = false) String name
    ) {
        // CORREÇÃO: Chamando o método correto 'searchByName'
        List<ProductResponseDTO> products = productService.searchByName(name, establishmentId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.canAccessProduct(#id)")
    public ResponseEntity<ProductResponseDTO> findById(@PathVariable Long id) {
        ProductResponseDTO product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityService.canAccessProduct(#id) and @securityService.hasAccessToEstablishment(#productRequestDTO.establishmentId)")
    public ResponseEntity<ProductResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO updated = productService.update(id, productRequestDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.canAccessProduct(#id)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
