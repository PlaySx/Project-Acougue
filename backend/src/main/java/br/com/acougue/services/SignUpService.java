package br.com.acougue.services;

import br.com.acougue.dto.SignUpRequestDTO;
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
public class SignUpService {

    private final UserRepository userRepository;
    private final EstablishmentRepository establishmentRepository;

    public SignUpService(UserRepository userRepository, EstablishmentRepository establishmentRepository) {
        this.userRepository = userRepository;
        this.establishmentRepository = establishmentRepository;
    }

    @Transactional
    public User signUp(SignUpRequestDTO data) {
        if (userRepository.findByEmail(data.getOwnerEmail()).isPresent()) {
            throw new DataIntegrityViolationException("Este email já está em uso.");
        }
        if (establishmentRepository.existsByCnpj(data.getEstablishmentCnpj())) {
            throw new DataIntegrityViolationException("Este CNPJ já está cadastrado.");
        }

        Establishment newEstablishment = new Establishment();
        newEstablishment.setName(data.getEstablishmentName());
        newEstablishment.setCnpj(data.getEstablishmentCnpj());
        Establishment savedEstablishment = establishmentRepository.save(newEstablishment);

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.getOwnerPassword());
        User newUser = new User(data.getOwnerEmail(), encryptedPassword, Role.ROLE_OWNER);
        newUser.setEstablishment(savedEstablishment);

        return userRepository.save(newUser);
    }
}
