package br.com.acougue.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.acougue.entities.Order;
import br.com.acougue.enums.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	
	// Busca pedidos por estabelecimento
    List<Order> findByEstablishmentId(Long establishmentId);
    
    // Busca pedidos por cliente
    List<Order> findByClientId(Long clientId);
    
    // Busca pedidos por status e estabelecimento
    List<Order> findByStatusAndEstablishmentId(OrderStatus status, Long establishmentId);
    
    // Busca pedidos por período e estabelecimento
    List<Order> findByDatahoraBetweenAndEstablishmentId(LocalDateTime start, LocalDateTime end, Long establishmentId);
    
    // Conta pedidos por estabelecimento
    @Query("SELECT COUNT(o) FROM Order o WHERE o.establishment.id = :establishmentId")
    Long countByEstablishmentId(@Param("establishmentId") Long establishmentId);
    
    // Busca pedidos recentes por estabelecimento (últimos 30 dias)
    @Query("SELECT o FROM Order o WHERE o.establishment.id = :establishmentId AND o.datahora >= :date ORDER BY o.datahora DESC")
    List<Order> findRecentOrdersByEstablishmentId(@Param("establishmentId") Long establishmentId, @Param("date") LocalDateTime date);

}
