package br.com.acougue.controller;

import br.com.acougue.dto.EstablishmentUpdateDTO;
import br.com.acougue.entities.Establishment;
import br.com.acougue.services.EstablishmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/establishments")
public class EstablishmentController {

    private final EstablishmentService establishmentService;

    public EstablishmentController(EstablishmentService establishmentService) {
        this.establishmentService = establishmentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Establishment> findById(@PathVariable Long id) {
        return ResponseEntity.ok(establishmentService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Establishment> update(@PathVariable Long id, @Valid @RequestBody EstablishmentUpdateDTO dto) {
        return ResponseEntity.ok(establishmentService.update(id, dto));
    }
}
