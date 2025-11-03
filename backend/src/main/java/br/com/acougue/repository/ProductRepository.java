package br.com.acougue.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.acougue.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	
	// Busca produtos por estabelecimento
    List<Product> findByEstablishmentId(Long establishmentId);
    
    // Busca produto por nome e estabelecimento
    List<Product> findByNameContainingIgnoreCaseAndEstablishmentId(String name, Long establishmentId);
    
    // Conta produtos por estabelecimento
    @Query("SELECT COUNT(p) FROM Product p WHERE p.establishment.id = :establishmentId")
    Long countByEstablishmentId(@Param("establishmentId") Long establishmentId);
    
    // Busca produtos por faixa de preço e estabelecimento
    List<Product> findByValueBetweenAndEstablishmentId(Double minValue, Double maxValue, Long establishmentId);
}
