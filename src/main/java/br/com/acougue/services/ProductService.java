package br.com.acougue.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.acougue.entities.Products;
import br.com.acougue.globalException.ProductNaoEncontradoException;
import br.com.acougue.repository.ProductsRepository;

@Service
public class ProductService {
	
	@Autowired
	private ProductsRepository productRepository;
	
    public Products salvar(Products product) {
        return productRepository.save(product);
    }

    public List<Products> listarTodos() {
        return productRepository.findAll();
    }

    public Optional<Products> buscarPorId(Long id) {
        return productRepository.findById(id);
    }

    public Products atualizar(Long id, Products productAtualizado) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(productAtualizado.getName());
                    product.setDescription(productAtualizado.getDescription());
                    product.setValue(productAtualizado.getValue());
                    return productRepository.save(product);
                })
                .orElseThrow(() -> new ProductNaoEncontradoException("Cliente não encontrado com id: " + id));
    }

    public void deletar(Long id) {
        productRepository.deleteById(id);
    }
}
