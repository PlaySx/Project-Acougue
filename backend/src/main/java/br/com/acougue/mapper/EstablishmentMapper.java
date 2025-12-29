package br.com.acougue.mapper;

import br.com.acougue.dto.EstablishmentUpdateDTO;
import br.com.acougue.entities.Establishment;
import org.springframework.stereotype.Component;

@Component
public class EstablishmentMapper {

    // No futuro, podemos ter DTOs de resposta aqui
    
    public void updateEntityFromDTO(Establishment entity, EstablishmentUpdateDTO dto) {
        if (entity == null || dto == null) return;
        
        entity.setName(dto.getName());
        entity.setAddress(dto.getAddress());
    }
}
