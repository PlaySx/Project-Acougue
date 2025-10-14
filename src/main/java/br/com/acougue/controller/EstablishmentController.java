package br.com.acougue.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.acougue.dto.EstablishmentCreateRequestDTO;
import br.com.acougue.dto.EstablishmentDTO;
import br.com.acougue.services.EstablishmentService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/establishments") // Padronizando para o inglês e plural
@Validated
public class EstablishmentController {

    private final EstablishmentService establishmentService;

    public EstablishmentController(EstablishmentService establishmentService) {
        this.establishmentService = establishmentService;
    }

    // ✅ NOVO: Criar establishment para um usuário específico
    @PostMapping("/create-for-user/{userId}")
    public ResponseEntity<EstablishmentDTO> createForUser(
            @PathVariable Long userId, 
            @Valid @RequestBody EstablishmentCreateRequestDTO requestDTO) {
        EstablishmentDTO created = establishmentService.createForUser(userId, requestDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @PostMapping
    public ResponseEntity<EstablishmentDTO> create(@Valid @RequestBody EstablishmentDTO establishmentDTO) {
        EstablishmentDTO created = establishmentService.create(establishmentDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @GetMapping
    public ResponseEntity<List<EstablishmentDTO>> findAll() {
        return ResponseEntity.ok(establishmentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstablishmentDTO> findById(@PathVariable Long id) {
        EstablishmentDTO establishment = establishmentService.findById(id);
        return ResponseEntity.ok(establishment);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<EstablishmentDTO>> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(establishmentService.findByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstablishmentDTO> update(@PathVariable Long id, @Valid @RequestBody EstablishmentDTO establishmentDTO) {
        EstablishmentDTO updated = establishmentService.update(id, establishmentDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        establishmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}