package br.com.acougue.controller;

import br.com.acougue.dto.DashboardDataDTO;
import br.com.acougue.services.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // 1. Importa
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    // 2. Aplica a proteção: Só permite se o usuário tiver acesso ao establishmentId solicitado
    @PreAuthorize("@securityService.hasAccessToEstablishment(#establishmentId)")
    public ResponseEntity<DashboardDataDTO> getDashboardData(
            @RequestParam Long establishmentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        DashboardDataDTO data = dashboardService.getDashboardData(establishmentId, startDate, endDate);
        return ResponseEntity.ok(data);
    }
}
