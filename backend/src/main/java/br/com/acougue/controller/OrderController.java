package br.com.acougue.controller;

import br.com.acougue.dto.OrderRequestDTO;
import br.com.acougue.dto.OrderResponseDTO;
import br.com.acougue.enums.OrderStatus;
import br.com.acougue.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/orders")
@Validated
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> create(@Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        OrderResponseDTO created = orderService.create(orderRequestDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> search(
            @RequestParam(name = "establishmentId") Long establishmentId,
            @RequestParam(name = "clientName", required = false) String clientName,
            @RequestParam(name = "status", required = false) OrderStatus status,
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<OrderResponseDTO> orders = orderService.search(establishmentId, clientName, status, date);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> findById(@PathVariable Long id) {
        OrderResponseDTO order = orderService.findById(id);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateStatus(@PathVariable Long id, @RequestBody String newStatus) {
        OrderStatus status = OrderStatus.valueOf(newStatus.toUpperCase());
        OrderResponseDTO updated = orderService.updateStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}