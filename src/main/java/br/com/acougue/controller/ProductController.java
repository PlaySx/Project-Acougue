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

import br.com.acougue.entities.Products;
import br.com.acougue.globalException.ProductNaoEncontradoException;
import br.com.acougue.services.ProductService;

@RestController
@RequestMapping("/produto")
public class ProductController {

	@Autowired
	private ProductService productService;
	
	 @PostMapping
	    public ResponseEntity<Products> salvar(@RequestBody Products product){
	        Products salvo = productService.salvar(product);
	        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
	    }
	 
	 @GetMapping
	    public List<Products> listarClient(){
	        return productService.listarTodos();
	    }
	 
	 @GetMapping("/{id}")
	    public ResponseEntity<Products> buscarPorId(@PathVariable Long id){
	        return productService.buscarPorId(id)
	                .map(ResponseEntity::ok)
	                .orElseThrow(() -> new ProductNaoEncontradoException("Cliente não encontrado com id: " + id));
	    }
	 
	 @PutMapping("/{id}")
	    public Products atualizarClient(@PathVariable Long id, @RequestBody Products productAtualizado) {
	        return productService.atualizar(id, productAtualizado);
	    }
	 
	 @DeleteMapping("/{id}")
	    public void deletarProduct(@PathVariable Long id) {
	        productService.deletar(id);
	    }
}
