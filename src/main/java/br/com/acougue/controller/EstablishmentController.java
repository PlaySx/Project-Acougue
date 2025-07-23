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

import br.com.acougue.entities.Establishment;
import br.com.acougue.globalException.EstablishmentNaoEncontradoException;
import br.com.acougue.services.EstablishmentService;

@RestController
@RequestMapping("/estabelecimento")
public class EstablishmentController {
	
	@Autowired
	private EstablishmentService establishmentService;
	
	@PostMapping
	public ResponseEntity<Establishment> salvar(@RequestBody Establishment establishment){
		Establishment salvo = establishmentService.salvar(establishment);
		return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
	}
	
	@GetMapping
	public List<Establishment> listarEstabelecimento(){
		return establishmentService.findAll();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Establishment> buscarPorId(@PathVariable Long id){
		return establishmentService.findById(id)
				.map(ResponseEntity::ok)
				.orElseThrow(() -> new EstablishmentNaoEncontradoException("Estabelecimento nao encontrado com o id: " + id));
	}
	
	@PutMapping("/{id}")
	public Establishment atualizarEstabelecimento(@PathVariable Long id, @RequestBody Establishment estabelecimentoAtualizado) {
		return establishmentService.update(id, estabelecimentoAtualizado);
	}
	
	@DeleteMapping("/{id}")
	public void deletarEstablishment(@PathVariable Long id) {
		establishmentService.deletar(id);
	}
	
}
