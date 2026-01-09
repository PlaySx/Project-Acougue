package br.com.acougue.controller;

import br.com.acougue.dto.ClientRequestDTO;
import br.com.acougue.dto.ClientResponseDTO;
import br.com.acougue.dto.ClientSummaryDTO;
import br.com.acougue.dto.OrderResponseDTO;
import br.com.acougue.services.ClientService;
import br.com.acougue.services.SecurityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;
    private final SecurityService securityService;

    public ClientController(ClientService clientService, SecurityService securityService) {
        this.clientService = clientService;
        this.securityService = securityService;
    }

    @PostMapping
    @PreAuthorize("@securityService.hasAccessToEstablishment(#requestDTO.establishmentId)")
    public ResponseEntity<ClientResponseDTO> create(@RequestBody @Valid ClientRequestDTO requestDTO) {
        ClientResponseDTO responseDTO = clientService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PostMapping("/import")
    @PreAuthorize("@securityService.hasAccessToEstablishment(#establishmentId)")
    public ResponseEntity<Map<String, Object>> importClients(
            @RequestParam("file") MultipartFile file,
            @RequestParam("establishmentId") Long establishmentId) {
        try {
            Map<String, Object> result = clientService.importClientsFromExcel(file, establishmentId);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erro ao processar arquivo: " + e.getMessage()));
        }
    }

    // NOVO ENDPOINT: Listagem leve com filtro de nome (Server-Side Filtering)
    @GetMapping("/summary")
    @PreAuthorize("@securityService.hasAccessToEstablishment(#establishmentId)")
    public ResponseEntity<List<ClientSummaryDTO>> listSummaries(
            @RequestParam Long establishmentId,
            @RequestParam(required = false) String name) { // Adicionado parâmetro name
        List<ClientSummaryDTO> summaries = clientService.listSummaries(establishmentId, name);
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/search")
    @PreAuthorize("@securityService.hasAccessToEstablishment(#establishmentId)")
    public ResponseEntity<List<ClientResponseDTO>> advancedSearch(
            @RequestParam Long establishmentId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String neighborhood,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        List<ClientResponseDTO> clients = clientService.advancedSearch(establishmentId, name, address, neighborhood, productName, startDate, endDate);
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.canAccessClient(#id)")
    public ResponseEntity<ClientResponseDTO> findById(@PathVariable Long id) {
        ClientResponseDTO client = clientService.findById(id);
        return ResponseEntity.ok(client);
    }
    
    @GetMapping("/{id}/orders")
    @PreAuthorize("@securityService.canAccessClient(#id)")
    public ResponseEntity<List<OrderResponseDTO>> findOrdersByClientId(@PathVariable Long id) {
        List<OrderResponseDTO> orders = clientService.findOrdersByClientId(id);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityService.canAccessClient(#id)")
    public ResponseEntity<ClientResponseDTO> update(@PathVariable Long id, @RequestBody @Valid ClientRequestDTO requestDTO) {
        ClientResponseDTO updatedClient = clientService.update(id, requestDTO);
        return ResponseEntity.ok(updatedClient);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.canAccessClient(#id)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
