package br.com.acougue.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.acougue.dto.EstablishmentAuthResponseDTO;
import br.com.acougue.dto.LoginDTO;
import br.com.acougue.dto.UserAuthResponseDTO;
import br.com.acougue.entities.Establishment;
import br.com.acougue.entities.User;
import br.com.acougue.repository.EstablishmentRepository;
import br.com.acougue.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Autentica um usuário (User)
     */
    public UserAuthResponseDTO authenticateUser(LoginDTO loginDTO) {
        if (loginDTO == null || loginDTO.getUsername() == null || loginDTO.getPassword() == null) {
            throw new IllegalArgumentException("Dados de login inválidos");
        }

        Optional<User> userOpt = userRepository.findByUsername(loginDTO.getUsername());
        
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        
        User user = userOpt.get();

        // Validar senha
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Senha inválida");
        }
        
        // Retornar dados seguros
        return new UserAuthResponseDTO(
            user.getId(),
            user.getUsername(),
            user.getRole().name(),
            user.getEstablishment() != null ? user.getEstablishment().getId() : null,
            user.getEstablishment() != null ? user.getEstablishment().getName() : null
        );
    }

    /**
     * Autentica um estabelecimento (Establishment)
     */
    public EstablishmentAuthResponseDTO authenticateEstablishment(LoginDTO loginDTO) {
        if (loginDTO == null || loginDTO.getUsername() == null || loginDTO.getPassword() == null) {
            throw new IllegalArgumentException("Dados de login inválidos");
        }
        
        Optional<Establishment> establishmentOpt = establishmentRepository.findByUsername(loginDTO.getUsername());
        
        if (establishmentOpt.isEmpty()) {
            throw new IllegalArgumentException("Estabelecimento não encontrado");
        }
        
        Establishment establishment = establishmentOpt.get();

        // Validar senha
        if (!passwordEncoder.matches(loginDTO.getPassword(), establishment.getPassword())) {
            throw new IllegalArgumentException("Senha inválida");
        }

        return new EstablishmentAuthResponseDTO(
            establishment.getId(), 
            establishment.getUsername(), 
            establishment.getName(), 
            establishment.getCnpj(), 
            establishment.getAddress()
        );
    }

    /**
     * Autentica automaticamente - tenta primeiro User, depois Establishment
     */
    public Object authenticate(LoginDTO loginDTO) {
        try {
            // Primeiro tenta autenticar como User
            return authenticateUser(loginDTO);
        } catch (IllegalArgumentException e) {
            // Se falhar, tenta como Establishment
            try {
                return authenticateEstablishment(loginDTO);
            } catch (IllegalArgumentException e2) {
                throw new IllegalArgumentException("Credenciais inválidas");
            }
        }
    }
}