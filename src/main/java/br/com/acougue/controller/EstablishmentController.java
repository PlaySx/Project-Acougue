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
	
	/**
	 * Cria um novo estabelecimento (sem autenticação)
	 * Para estabelecimentos com login, use /auth/register
	 */
	@PostMapping
	public ResponseEntity<EstablishmentDTO> create(@RequestBody EstablishmentDTO establishmentDTO){
		try {
			EstablishmentDTO created = establishmentService.create(establishmentDTO);
			return ResponseEntity.status(HttpStatus.CREATED).body(created);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	/**
	 * Lista todos os estabelecimentos
	 */
	@GetMapping
	public ResponseEntity<List<EstablishmentDTO>> findAll(){
		try {
			List<EstablishmentDTO> establishments = establishmentService.findAll();
			return ResponseEntity.ok(establishments);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	/**
	 * Busca estabelecimento por ID
	 */
	@GetMapping("/{id}")
	public ResponseEntity<EstablishmentDTO> findById(@PathVariable Long id){
		try {
			return establishmentService.findById(id)
					.map(ResponseEntity::ok)
					.orElseThrow(() -> new EstablishmentNaoEncontradoException("Estabelecimento não encontrado com o id: " + id));
		} catch (EstablishmentNaoEncontradoException e) {
			throw e; // Deixa o GlobalExceptionHandler tratar
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	/**
	 * Atualiza estabelecimento (não atualiza username nem senha)
	 */
	@PutMapping("/{id}")
	public ResponseEntity<EstablishmentDTO> update(@PathVariable Long id, @RequestBody EstablishmentDTO establishmentDTO) {
		try {
			EstablishmentDTO updated = establishmentService.update(id, establishmentDTO);
			return ResponseEntity.ok(updated);
		} catch (EstablishmentNaoEncontradoException e) {
			throw e; // Deixa o GlobalExceptionHandler tratar
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	/**
	 * Deleta estabelecimento
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		try {
			establishmentService.delete(id);
			return ResponseEntity.noContent().build();
		} catch (EstablishmentNaoEncontradoException e) {
			throw e; // Deixa o GlobalExceptionHandler tratar
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}