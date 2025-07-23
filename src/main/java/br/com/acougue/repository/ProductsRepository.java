package br.com.acougue.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.acougue.entities.Products;

public interface ProductsRepository extends JpaRepository<Products, Long> {

}
