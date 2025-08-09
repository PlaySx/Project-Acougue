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
import org.springframework.web.bind.annotation.RestController;

import br.com.acougue.dto.EstablishmentDTO;
import br.com.acougue.globalException.EstablishmentNaoEncontradoException;
import br.com.acougue.services.EstablishmentService;

@RestController
@RequestMapping("/estabelecimento")
public class EstablishmentController {
	
	@Autowired
	private EstablishmentService establishmentService;
	
	@PostMapping
	public ResponseEntity<EstablishmentDTO> create(@RequestBody EstablishmentDTO establishmentDTO){
		EstablishmentDTO created = establishmentService.create(establishmentDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}
	
	@GetMapping
	public List<EstablishmentDTO> findAll(){
		return establishmentService.findAll();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<EstablishmentDTO> findById(@PathVariable Long id){
		return establishmentService.findById(id)
				.map(ResponseEntity::ok)
				.orElseThrow(() -> new EstablishmentNaoEncontradoException("Estabelecimento não encontrado com o id: " + id));
	}
	
	@PutMapping("/{id}")
	public EstablishmentDTO update(@PathVariable Long id, @RequestBody EstablishmentDTO establishmentDTO) {
		return establishmentService.update(id, establishmentDTO);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		establishmentService.delete(id);
		return ResponseEntity.noContent().build();
	}
}