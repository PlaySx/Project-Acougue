package br.com.acougue.repository;

import br.com.acougue.entities.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    // Busca logs por estabelecimento, ordenados do mais recente para o mais antigo
    List<AuditLog> findByEstablishmentIdOrderByTimestampDesc(Long establishmentId);
}
