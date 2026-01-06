package br.com.acougue.services;

import br.com.acougue.dto.RegisterRequestDTO;
import br.com.acougue.entities.Establishment;
import br.com.acougue.entities.User;
import br.com.acougue.enums.Role;
import br.com.acougue.repository.EstablishmentRepository;
import br.com.acougue.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final EstablishmentRepository establishmentRepository;

    public RegistrationService(UserRepository userRepository, EstablishmentRepository establishmentRepository) {
        this.userRepository = userRepository;
        this.establishmentRepository = establishmentRepository;
    }

    @Transactional
    public User register(RegisterRequestDTO data) {
        if (userRepository.findByEmail(data.getEmail()).isPresent()) {
            throw new DataIntegrityViolationException("Este email já está em uso.");
        }

        User newUser = new User(data.getEmail(), new BCryptPasswordEncoder().encode(data.getPassword()), data.getRole());

        if (data.getRole() == Role.ROLE_OWNER) {
            // Lógica para Proprietário
            if (data.getEstablishmentName() == null || data.getEstablishmentCnpj() == null) {
                throw new IllegalArgumentException("Nome do estabelecimento e CNPJ são obrigatórios para proprietários.");
            }
            if (establishmentRepository.existsByCnpj(data.getEstablishmentCnpj())) {
                throw new DataIntegrityViolationException("Este CNPJ já está cadastrado.");
            }
            
            Establishment newEstablishment = new Establishment();
            newEstablishment.setName(data.getEstablishmentName());
            newEstablishment.setCnpj(data.getEstablishmentCnpj());
            // CORREÇÃO: Salvando o endereço
            newEstablishment.setAddress(data.getEstablishmentAddress() != null ? data.getEstablishmentAddress() : "Endereço não informado"); 
            
            Establishment savedEstablishment = establishmentRepository.save(newEstablishment);
            
            newUser.setEstablishment(savedEstablishment);

        } else if (data.getRole() == Role.ROLE_EMPLOYEE) {
            // Lógica para Funcionário
            if (data.getEstablishmentId() == null) {
                throw new IllegalArgumentException("O ID do estabelecimento é obrigatório para funcionários.");
            }
            Establishment establishment = establishmentRepository.findById(data.getEstablishmentId())
                .orElseThrow(() -> new DataIntegrityViolationException("Estabelecimento não encontrado."));
            
            newUser.setEstablishment(establishment);
        }

        return userRepository.save(newUser);
    }
}
