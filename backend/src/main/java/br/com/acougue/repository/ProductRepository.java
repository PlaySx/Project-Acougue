package br.com.acougue.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.acougue.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	
    List<Product> findByEstablishmentId(Long establishmentId);
    
    List<Product> findByNameContainingIgnoreCaseAndEstablishmentId(String name, Long establishmentId);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.establishment.id = :establishmentId")
    Long countByEstablishmentId(@Param("establishmentId") Long establishmentId);
    
    // Corrigido para usar o novo nome do campo 'unitPrice'
    List<Product> findByUnitPriceBetweenAndEstablishmentId(BigDecimal minValue, BigDecimal maxValue, Long establishmentId);
}
