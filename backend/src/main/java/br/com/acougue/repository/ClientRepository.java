package br.com.acougue.repository;

import br.com.acougue.dto.ClientSummaryDTO;
import br.com.acougue.entities.Client;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByPhoneNumbersNumber(String number);
    
    boolean existsByNameAndPhoneNumbersIsNull(String name);

    @Query("SELECT COUNT(c) FROM Client c WHERE c.establishment.id = :establishmentId")
    Long countByEstablishmentId(@Param("establishmentId") Long establishmentId);

    @Query("SELECT COUNT(c) FROM Client c WHERE c.establishment.id = :establishmentId AND c.createdAt BETWEEN :start AND :end")
    Long countByEstablishmentIdAndCreatedAtBetween(@Param("establishmentId") Long establishmentId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // QUERY OTIMIZADA COM FILTRO DE NOME (Server-Side Filtering)
    @Query("SELECT DISTINCT new br.com.acougue.dto.ClientSummaryDTO(c.id, c.name, ph.number, c.address, c.addressNeighborhood) " +
           "FROM Client c LEFT JOIN c.phoneNumbers ph " +
           "WHERE c.establishment.id = :establishmentId " +
           "AND (:name IS NULL OR LOWER(CAST(c.name AS string)) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%')))")
    List<ClientSummaryDTO> findClientSummariesByEstablishmentIdAndName(@Param("establishmentId") Long establishmentId, @Param("name") String name, Pageable pageable);

    // Mantendo o método antigo para compatibilidade se necessário (sem filtro de nome)
    @Query("SELECT DISTINCT new br.com.acougue.dto.ClientSummaryDTO(c.id, c.name, ph.number, c.address, c.addressNeighborhood) " +
           "FROM Client c LEFT JOIN c.phoneNumbers ph " +
           "WHERE c.establishment.id = :establishmentId")
    List<ClientSummaryDTO> findClientSummariesByEstablishmentId(@Param("establishmentId") Long establishmentId);

    @Query("SELECT DISTINCT c FROM Client c " +
           "LEFT JOIN FETCH c.phoneNumbers " +
           "LEFT JOIN FETCH c.establishment " +
           "LEFT JOIN c.orders o " +
           "LEFT JOIN o.items oi " +
           "LEFT JOIN oi.product p " +
           "WHERE c.establishment.id = :establishmentId " +
           "AND (:name IS NULL OR LOWER(CAST(c.name AS string)) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%'))) " +
           "AND (:address IS NULL OR LOWER(CAST(c.address AS string)) LIKE LOWER(CONCAT('%', CAST(:address AS string), '%'))) " +
           "AND (:neighborhood IS NULL OR LOWER(CAST(c.addressNeighborhood AS string)) LIKE LOWER(CONCAT('%', CAST(:neighborhood AS string), '%'))) " +
           "AND (:productName IS NULL OR LOWER(CAST(p.name AS string)) LIKE LOWER(CONCAT('%', CAST(:productName AS string), '%'))) " +
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
