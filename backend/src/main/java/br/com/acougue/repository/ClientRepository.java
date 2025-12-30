package br.com.acougue.repository;

import br.com.acougue.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByPhoneNumbersNumber(String number);
    
    // Novo método para evitar duplicatas de clientes sem telefone
    boolean existsByNameAndPhoneNumbersIsNull(String name);

    @Query("SELECT COUNT(c) FROM Client c WHERE c.establishment.id = :establishmentId")
    Long countByEstablishmentId(@Param("establishmentId") Long establishmentId);

    @Query("SELECT COUNT(c) FROM Client c WHERE c.establishment.id = :establishmentId AND c.createdAt BETWEEN :start AND :end")
    Long countByEstablishmentIdAndCreatedAtBetween(@Param("establishmentId") Long establishmentId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT DISTINCT c FROM Client c " +
           "LEFT JOIN c.orders o " +
           "LEFT JOIN o.items oi " +
           "LEFT JOIN oi.product p " +
           "WHERE c.establishment.id = :establishmentId " +
           "AND (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:address IS NULL OR LOWER(c.address) LIKE LOWER(CONCAT('%', :address, '%'))) " +
           "AND (:neighborhood IS NULL OR LOWER(c.addressNeighborhood) LIKE LOWER(CONCAT('%', :neighborhood, '%'))) " +
           "AND (:productName IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :productName, '%'))) " +
           "AND (cast(:startDate as timestamp) IS NULL OR o.datahora >= :startDate) " +
           "AND (cast(:endDate as timestamp) IS NULL OR o.datahora <= :endDate)")
    List<Client> findByAdvancedFilters(
            @Param("establishmentId") Long establishmentId,
            @Param("name") String name,
            @Param("address") String address,
            @Param("neighborhood") String neighborhood,
            @Param("productName") String productName,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
