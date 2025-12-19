package br.com.acougue.controller;

import br.com.acougue.dto.DashboardDataDTO;
import br.com.acougue.services.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardDataDTO> getDashboardData(@RequestParam Long establishmentId) {
        DashboardDataDTO data = dashboardService.getDashboardData(establishmentId);
        return ResponseEntity.ok(data);
    }
}