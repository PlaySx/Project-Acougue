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
	
    // CORREÇÃO DE PERFORMANCE: JOIN FETCH para trazer o estabelecimento junto
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.establishment WHERE p.establishment.id = :establishmentId")
    List<Product> findByEstablishmentId(@Param("establishmentId") Long establishmentId);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.establishment WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.establishment.id = :establishmentId")
    List<Product> findByNameContainingIgnoreCaseAndEstablishmentId(@Param("name") String name, @Param("establishmentId") Long establishmentId);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.establishment.id = :establishmentId")
    Long countByEstablishmentId(@Param("establishmentId") Long establishmentId);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.establishment WHERE p.unitPrice BETWEEN :minValue AND :maxValue AND p.establishment.id = :establishmentId")
    List<Product> findByUnitPriceBetweenAndEstablishmentId(@Param("minValue") BigDecimal minValue, @Param("maxValue") BigDecimal maxValue, @Param("establishmentId") Long establishmentId);
}
