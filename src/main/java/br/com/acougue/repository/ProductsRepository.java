package br.com.acougue.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.acougue.entities.Products;

@Repository
public interface ProductsRepository extends JpaRepository<Products, Long> {
	
	// Busca produtos por estabelecimento
    List<Products> findByEstablishmentId(Long establishmentId);
    
    // Busca produto por nome e estabelecimento
    List<Products> findByNameContainingIgnoreCaseAndEstablishmentId(String name, Long establishmentId);
    
    // Conta produtos por estabelecimento
    @Query("SELECT COUNT(p) FROM Products p WHERE p.establishment.id = :establishmentId")
    Long countByEstablishmentId(@Param("establishmentId") Long establishmentId);
    
    // Busca produtos por faixa de preço e estabelecimento
    List<Products> findByValueBetweenAndEstablishmentId(Double minValue, Double maxValue, Long establishmentId);
}

