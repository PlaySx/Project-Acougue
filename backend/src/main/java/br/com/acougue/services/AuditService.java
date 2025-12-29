package br.com.acougue.services;

import br.com.acougue.entities.AuditLog;
import br.com.acougue.repository.AuditLogRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // Usa uma nova transação para garantir que o log seja salvo mesmo se a operação principal falhar (opcional, mas recomendado para logs de erro)
    // Ou usa a mesma transação para garantir consistência (se a operação falhar, o log não é salvo). Vamos usar a mesma por enquanto.
    @Transactional(propagation = Propagation.REQUIRED)
    public void logAction(String action, String entityName, String entityId, String details, Long establishmentId) {
        String username = "SYSTEM";
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            // O principal pode ser um objeto User ou uma String (email), dependendo de como configuramos o filtro
            username = authentication.getName(); 
        }

        AuditLog log = new AuditLog(username, action, entityName, entityId, details, establishmentId);
        auditLogRepository.save(log);
    }

    public List<AuditLog> getLogsByEstablishment(Long establishmentId) {
        return auditLogRepository.findByEstablishmentIdOrderByTimestampDesc(establishmentId);
    }
}
