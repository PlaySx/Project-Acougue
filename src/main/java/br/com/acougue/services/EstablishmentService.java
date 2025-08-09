package br.com.acougue.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.acougue.dto.EstablishmentDTO;
import br.com.acougue.entities.Establishment;
import br.com.acougue.globalException.EstablishmentNaoEncontradoException;
import br.com.acougue.mapper.EstablishmentMapper;
import br.com.acougue.repository.EstablishmentRepository;

@Service
public class EstablishmentService {
	
	@Autowired
	private EstablishmentRepository establishmentRepository;
	
	@Autowired
	private EstablishmentMapper establishmentMapper;
	
	@Transactional
	public EstablishmentDTO create(EstablishmentDTO establishmentDTO) {
		Establishment establishment = establishmentMapper.toEntity(establishmentDTO);
		Establishment savedEstablishment = establishmentRepository.save(establishment);
		return establishmentMapper.toDTO(savedEstablishment);
	}
	
	public List<EstablishmentDTO> findAll(){
		List<Establishment> establishments = establishmentRepository.findAll();
		return establishmentMapper.toDTOList(establishments);
	}
	
	public Optional<EstablishmentDTO> findById(Long id){
		return establishmentRepository.findById(id)
				.map(establishmentMapper::toDTO);
	}
	
	@Transactional
	public EstablishmentDTO update(Long id, EstablishmentDTO establishmentDTO) {
		Establishment existingEstablishment = establishmentRepository.findById(id)
				.orElseThrow(() -> new EstablishmentNaoEncontradoException("Estabelecimento não encontrado com id: " + id));
		
		establishmentMapper.updateEntityFromDTO(existingEstablishment, establishmentDTO);
		Establishment updatedEstablishment = establishmentRepository.save(existingEstablishment);
		return establishmentMapper.toDTO(updatedEstablishment);
	}
	
	@Transactional
	public void delete(Long id) {
		if (!establishmentRepository.existsById(id)) {
            throw new EstablishmentNaoEncontradoException("Estabelecimento não encontrado com id: " + id);
        }
		establishmentRepository.deleteById(id);
    }
}