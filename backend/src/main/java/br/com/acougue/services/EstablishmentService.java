package br.com.acougue.services;

import br.com.acougue.dto.EstablishmentUpdateDTO;
import br.com.acougue.entities.Establishment;
import br.com.acougue.exceptions.ResourceNotFoundException;
import br.com.acougue.mapper.EstablishmentMapper;
import br.com.acougue.repository.EstablishmentRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EstablishmentService {

    private final EstablishmentRepository establishmentRepository;
    private final EstablishmentMapper establishmentMapper;
    private final AuditService auditService;

    public EstablishmentService(EstablishmentRepository establishmentRepository, EstablishmentMapper establishmentMapper, AuditService auditService) {
        this.establishmentRepository = establishmentRepository;
        this.establishmentMapper = establishmentMapper;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public Establishment findById(Long id) {
        return establishmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado com o ID: " + id));
    }

    @Transactional
    @PreAuthorize("hasRole('OWNER')")
    public Establishment update(Long id, EstablishmentUpdateDTO dto) {
        Establishment existingEstablishment = findById(id);
        
        String oldDetails = "Nome: " + existingEstablishment.getName() + ", Endereço: " + existingEstablishment.getAddress();
        
        establishmentMapper.updateEntityFromDTO(existingEstablishment, dto);
        Establishment updatedEstablishment = establishmentRepository.save(existingEstablishment);

        String newDetails = "Nome: " + updatedEstablishment.getName() + ", Endereço: " + updatedEstablishment.getAddress();
        
        // CORREÇÃO: O último parâmetro é o ID do estabelecimento, que é o próprio 'id' neste caso.
        auditService.logAction("UPDATE", "Establishment", id.toString(), "De: [" + oldDetails + "] Para: [" + newDetails + "]", id);

        return updatedEstablishment;
    }
}
