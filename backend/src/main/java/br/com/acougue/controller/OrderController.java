package br.com.acougue.controller;

import br.com.acougue.dto.OrderRequestDTO;
import br.com.acougue.dto.OrderResponseDTO;
import br.com.acougue.enums.OrderStatus;
import br.com.acougue.services.OrderService;
import br.com.acougue.services.ReportService;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/orders")
@Validated
public class OrderController {

    private final OrderService orderService;
    private final ReportService reportService;

    public OrderController(OrderService orderService, ReportService reportService) {
        this.orderService = orderService;
        this.reportService = reportService;
    }

    @PostMapping
    @PreAuthorize("@securityService.hasAccessToEstablishment(#orderRequestDTO.establishmentId)")
    public ResponseEntity<OrderResponseDTO> create(@Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        OrderResponseDTO created = orderService.create(orderRequestDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @GetMapping
    @PreAuthorize("@securityService.hasAccessToEstablishment(#establishmentId)")
    public ResponseEntity<List<OrderResponseDTO>> search(
            @RequestParam(name = "establishmentId") Long establishmentId,
            @RequestParam(name = "clientName", required = false) String clientName,
            @RequestParam(name = "status", required = false) OrderStatus status,
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<OrderResponseDTO> orders = orderService.findByFilters(establishmentId, clientName, status, date);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/export/excel")
    @PreAuthorize("@securityService.hasAccessToEstablishment(#establishmentId)")
    public ResponseEntity<InputStreamResource> exportToExcel(@RequestParam Long establishmentId) throws IOException {
        ByteArrayInputStream in = reportService.exportOrdersToExcel(establishmentId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=pedidos.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.canAccessOrder(#id)")
    public ResponseEntity<OrderResponseDTO> findById(@PathVariable Long id) {
        OrderResponseDTO order = orderService.findById(id);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("@securityService.canAccessOrder(#id)")
    public ResponseEntity<OrderResponseDTO> updateStatus(@PathVariable Long id, @RequestBody String newStatus) {
        OrderResponseDTO updated = orderService.updateStatus(id, newStatus);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.canAccessOrder(#id)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
