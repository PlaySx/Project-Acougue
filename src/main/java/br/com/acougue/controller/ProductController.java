package br.com.acougue.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.acougue.dto.ProductRequestDTO;
import br.com.acougue.dto.ProductResponseDTO;
import br.com.acougue.globalException.ProductNaoEncontradoException;
import br.com.acougue.services.ProductService;

@RestController
@RequestMapping("/produto")
public class ProductController {

	@Autowired
	private ProductService productService;
	
	@PostMapping
	public ResponseEntity<ProductResponseDTO> create(@RequestBody ProductRequestDTO productRequestDTO){
		ProductResponseDTO created = productService.create(productRequestDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}
	 
	@GetMapping
	public List<ProductResponseDTO> findAll(){
		return productService.findAll();
	}
	 
	@GetMapping("/{id}")
	public ResponseEntity<ProductResponseDTO> findById(@PathVariable Long id){
		return productService.findById(id)
				.map(ResponseEntity::ok)
				.orElseThrow(() -> new ProductNaoEncontradoException("Produto não encontrado com id: " + id));
	}
	 
	@PutMapping("/{id}")
	public ProductResponseDTO update(@PathVariable Long id, @RequestBody ProductRequestDTO productRequestDTO) {
		return productService.update(id, productRequestDTO);
	}
	 
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		productService.delete(id);
		return ResponseEntity.noContent().build();
	}
}