package br.com.acougue.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.acougue.dto.OrderRequestDTO;
import br.com.acougue.dto.OrderResponseDTO;
import br.com.acougue.enums.OrderStatus;
import br.com.acougue.services.OrderService;

@RestController
@RequestMapping("/orders")
public class ProductController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> create(@RequestBody OrderRequestDTO requestDTO) {
        try {
            OrderResponseDTO response = orderService.create(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public List<OrderResponseDTO> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> findById(@PathVariable Long id) {
        return orderService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/establishment/{establishmentId}")
    public List<OrderResponseDTO> findByEstablishmentId(@PathVariable Long establishmentId) {
        return orderService.findByEstablishmentId(establishmentId);
    }

    @GetMapping("/client/{clientId}")
    public List<OrderResponseDTO> findByClientId(@PathVariable Long clientId) {
        return orderService.findByClientId(clientId);
    }

    @GetMapping("/status")
    public List<OrderResponseDTO> findByStatus(
            @RequestParam OrderStatus status,
            @RequestParam Long establishmentId) {
        return orderService.findByStatus(status, establishmentId);
    }

    @GetMapping("/recent/{establishmentId}")
    public List<OrderResponseDTO> findRecentOrders(@PathVariable Long establishmentId) {
        return orderService.findRecentOrders(establishmentId);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        try {
            OrderResponseDTO updated = orderService.updateStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> update(
            @PathVariable Long id, 
            @RequestBody OrderRequestDTO requestDTO) {
        try {
            OrderResponseDTO updated = orderService.update(id, requestDTO);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            orderService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}