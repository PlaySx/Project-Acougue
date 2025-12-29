package br.com.acougue.repository;

import br.com.acougue.dto.DailyRevenueDTO;
import br.com.acougue.dto.TopProductDTO;
import br.com.acougue.entities.Order;
import br.com.acougue.enums.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByEstablishmentId(Long establishmentId);
    List<Order> findByClientId(Long clientId);
    List<Order> findByStatusAndEstablishmentId(OrderStatus status, Long establishmentId);
    
    // Método padronizado
    List<Order> findByEstablishmentIdAndDatahoraBetween(Long establishmentId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.establishment.id = :establishmentId")
    Long countByEstablishmentId(@Param("establishmentId") Long establishmentId);

    // Pedidos recentes (com filtro de data opcional na query, mas aqui fixo para simplicidade do método)
    @Query("SELECT o FROM Order o WHERE o.establishment.id = :establishmentId AND o.datahora >= :date ORDER BY o.datahora DESC")
    List<Order> findRecentOrdersByEstablishmentId(@Param("establishmentId") Long establishmentId, @Param("date") LocalDateTime date);

    // KPIs com intervalo de data
    @Query("SELECT COUNT(o) FROM Order o WHERE o.establishment.id = :establishmentId AND o.datahora BETWEEN :start AND :end")
    Long countByEstablishmentIdAndDatahoraBetween(@Param("establishmentId") Long establishmentId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(o.totalValue) FROM Order o WHERE o.establishment.id = :establishmentId AND o.datahora BETWEEN :start AND :end")
    BigDecimal sumTotalValueByEstablishmentIdAndDatahoraBetween(@Param("establishmentId") Long establishmentId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Status com intervalo de data (ATUALIZADO)
    @Query("SELECT o.status, COUNT(o) FROM Order o WHERE o.establishment.id = :establishmentId AND o.datahora BETWEEN :start AND :end GROUP BY o.status")
    Map<OrderStatus, Long> countByEstablishmentIdAndDatahoraBetweenGroupByStatus(@Param("establishmentId") Long establishmentId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Top Produtos com intervalo de data (ATUALIZADO)
    @Query("SELECT new br.com.acougue.dto.TopProductDTO(oi.product.name, COUNT(oi.product.id)) " +
           "FROM OrderItem oi WHERE oi.order.establishment.id = :establishmentId " +
           "AND oi.order.datahora BETWEEN :start AND :end " +
           "GROUP BY oi.product.name " +
           "ORDER BY COUNT(oi.product.id) DESC")
    List<TopProductDTO> findTopSellingProducts(@Param("establishmentId") Long establishmentId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);

    // Faturamento Diário com intervalo de data (ATUALIZADO)
    @Query("SELECT new br.com.acougue.dto.DailyRevenueDTO(CAST(o.datahora AS LocalDate), SUM(o.totalValue)) " +
           "FROM Order o " +
           "WHERE o.establishment.id = :establishmentId AND o.datahora BETWEEN :start AND :end " +
           "GROUP BY CAST(o.datahora AS LocalDate) " +
           "ORDER BY CAST(o.datahora AS LocalDate) ASC")
    List<DailyRevenueDTO> findDailyRevenue(@Param("establishmentId") Long establishmentId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
