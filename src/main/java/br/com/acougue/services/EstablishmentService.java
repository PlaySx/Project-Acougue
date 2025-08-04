package br.com.acougue.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.acougue.entities.Establishment;
import br.com.acougue.globalException.ProductNaoEncontradoException;
import br.com.acougue.repository.EstablishmentRepository;

@Service
public class EstablishmentService {
	
	@Autowired
	private EstablishmentRepository establishmentRepository;
	
	public Establishment salvar(Establishment establishment) {
		return establishmentRepository.save(establishment);
	}
	
	public List<Establishment> findAll(){
		return establishmentRepository.findAll();
	}
	
	public Optional<Establishment> findById(Long id){
		return establishmentRepository.findById(id);
	}
	
	public Establishment update(Long id, Establishment establishmentAtualizado) {
		return establishmentRepository.findById(id)
				.map(establishment ->{
					establishment.setName(establishmentAtualizado.getName());
					establishment.setCnpj(establishment.getCnpj());
					establishment.setAdress(establishment.getAdress());
					return establishmentRepository.save(establishment);
				})
		 .orElseThrow(() -> new ProductNaoEncontradoException("Estabelecimento não encontrado com id: " + id));
	}
	
	public void deletar(Long id) {
       establishmentRepository.deleteById(id);
    }
}
