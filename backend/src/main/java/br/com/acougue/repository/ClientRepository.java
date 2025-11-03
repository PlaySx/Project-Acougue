package br.com.acougue.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.acougue.entities.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
	
	 // Busca clientes por estabelecimento
    List<Client> findByEstablishmentId(Long establishmentId);
    
    // Busca cliente por nome e estabelecimento
    List<Client> findByNameContainingIgnoreCaseAndEstablishmentId(String name, Long establishmentId);
    
    // Conta clientes por estabelecimento
    @Query("SELECT COUNT(c) FROM Client c WHERE c.establishment.id = :establishmentId")
    Long countByEstablishmentId(@Param("establishmentId") Long establishmentId);
    
    // Verifica se existe cliente com mesmo telefone no estabelecimento
    boolean existsByNumberPhoneAndEstablishmentId(Long numberPhone, Long establishmentId);
}

