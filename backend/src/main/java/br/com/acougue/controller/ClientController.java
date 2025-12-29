package br.com.acougue.controller;

import br.com.acougue.dto.ClientRequestDTO;
import br.com.acougue.dto.ClientResponseDTO;
import br.com.acougue.services.ClientService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/clients")
@Validated
public class ClientController {

	private final ClientService clientService;

	public ClientController(ClientService clientService) {
		this.clientService = clientService;
	}

	@PostMapping
	public ResponseEntity<ClientResponseDTO> create(@Valid @RequestBody ClientRequestDTO clientRequestDTO) {
		ClientResponseDTO created = clientService.create(clientRequestDTO);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(created.getId()).toUri();
		return ResponseEntity.created(uri).body(created);
	}

	@PostMapping("/import")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Map<String, Object>> importClients(
            @RequestParam("file") MultipartFile file,
            @RequestParam("establishmentId") Long establishmentId) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "O arquivo não pode estar vazio."));
        }
        try {
            Map<String, Object> result = clientService.importClientsFromExcel(file, establishmentId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Falha ao processar o arquivo: " + e.getMessage()));
        }
    }

	@GetMapping("/advanced-search")
	public ResponseEntity<List<ClientResponseDTO>> advancedSearch(
			@RequestParam @NotNull Long establishmentId,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String address,
			@RequestParam(required = false) String neighborhood,
			@RequestParam(required = false) String productName,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
	) {
		List<ClientResponseDTO> clients = clientService.advancedSearch(establishmentId, name, address, neighborhood, productName, startDate, endDate);
		return ResponseEntity.ok(clients);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ClientResponseDTO> findById(@PathVariable Long id) {
		ClientResponseDTO client = clientService.findById(id);
		return ResponseEntity.ok(client);
	}

	@PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> update(
            @PathVariable Long id, 
            @Valid @RequestBody ClientRequestDTO requestDTO) {
        ClientResponseDTO updated = clientService.update(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

	@DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
