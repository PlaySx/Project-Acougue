
package br.com.acougue.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.acougue.dto.ClientRequestDTO;
import br.com.acougue.dto.ClientResponseDTO;
import br.com.acougue.globalException.ClienteNaoEncontradoException;
import br.com.acougue.services.ClientService;

@RestController
@RequestMapping("/cliente")
public class ClientController {

	@Autowired
	private ClientService clientService;

	@PostMapping
	public ResponseEntity<ClientResponseDTO> create(@RequestBody ClientRequestDTO clientRequestDTO) {
		ClientResponseDTO created = clientService.create(clientRequestDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@GetMapping
	public List<ClientResponseDTO> findAll() {
		return clientService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<ClientResponseDTO> findById(@PathVariable Long id) {
		return clientService.findById(id).map(ResponseEntity::ok)
				.orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado com id: " + id));
	}

	@GetMapping("/establishment/{establishmentId}")
	public List<ClientResponseDTO> findByEstablishmentId(@PathVariable Long establishmentId) {
		return clientService.findByEstablishmentId(establishmentId);
	}

	@GetMapping("/search")
	public List<ClientResponseDTO> searchByName(@RequestParam String name, @RequestParam Long establishmentId) {
		return clientService.searchByName(name, establishmentId);
	}

	@PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> update(
            @PathVariable Long id, 
            @RequestBody ClientRequestDTO requestDTO) {
        try {
            ClientResponseDTO updated = clientService.update(id, requestDTO);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

	@DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            clientService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
       
 }
}
}