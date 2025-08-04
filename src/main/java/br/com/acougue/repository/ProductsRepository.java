package br.com.acougue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.acougue.entities.Products;

@Repository
public interface ProductsRepository extends JpaRepository<Products, Long> {

}
