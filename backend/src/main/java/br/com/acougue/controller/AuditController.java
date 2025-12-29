package br.com.acougue.controller;

import br.com.acougue.entities.AuditLog;
import br.com.acougue.services.AuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/audit")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    @PreAuthorize("hasRole('OWNER')") // Apenas proprietários podem ver os logs
    public ResponseEntity<List<AuditLog>> getLogs(@RequestParam Long establishmentId) {
        List<AuditLog> logs = auditService.getLogsByEstablishment(establishmentId);
        return ResponseEntity.ok(logs);
    }
}
