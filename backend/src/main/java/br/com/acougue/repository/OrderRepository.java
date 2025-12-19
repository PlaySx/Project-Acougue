package br.com.acougue.repository;

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
    List<Order> findByEstablishmentIdAndDatahoraBetween(Long establishmentId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.establishment.id = :establishmentId")
    Long countByEstablishmentId(@Param("establishmentId") Long establishmentId);

    @Query("SELECT o FROM Order o WHERE o.establishment.id = :establishmentId AND o.datahora >= :date ORDER BY o.datahora DESC")
    List<Order> findRecentOrdersByEstablishmentId(@Param("establishmentId") Long establishmentId, @Param("date") LocalDateTime date);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.establishment.id = :establishmentId AND o.datahora BETWEEN :start AND :end")
    Long countByEstablishmentIdAndDatahoraBetween(@Param("establishmentId") Long establishmentId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(o.totalValue) FROM Order o WHERE o.establishment.id = :establishmentId AND o.datahora BETWEEN :start AND :end")
    BigDecimal sumTotalValueByEstablishmentIdAndDatahoraBetween(@Param("establishmentId") Long establishmentId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT o.status, COUNT(o) FROM Order o WHERE o.establishment.id = :establishmentId GROUP BY o.status")
    Map<OrderStatus, Long> countByEstablishmentIdGroupByStatus(@Param("establishmentId") Long establishmentId);

    // A query de top produtos continua válida, pois conta as ocorrências do produto nos itens
    @Query("SELECT new br.com.acougue.dto.TopProductDTO(oi.product.name, COUNT(oi.product.id)) " +
           "FROM OrderItem oi WHERE oi.order.establishment.id = :establishmentId " +
           "GROUP BY oi.product.name " +
           "ORDER BY COUNT(oi.product.id) DESC")
    List<TopProductDTO> findTopSellingProducts(@Param("establishmentId") Long establishmentId, Pageable pageable);
}
