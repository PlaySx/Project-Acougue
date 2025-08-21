package br.com.acougue.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.acougue.dto.UserDTO;
import br.com.acougue.dto.UserRegisterDTO;
import br.com.acougue.dto.UserAuthResponseDTO;
import br.com.acougue.dto.LoginDTO;
import br.com.acougue.entities.User;
import br.com.acougue.entities.Establishment;
import br.com.acougue.mapper.UserMapper;
import br.com.acougue.repository.UserRepository;
import br.com.acougue.repository.EstablishmentRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserAuthResponseDTO registerUser(UserRegisterDTO registerDTO) {
        // Validações básicas
        if (registerDTO == null) {
            throw new IllegalArgumentException("Dados de registro não podem ser nulos");
        }
        if (registerDTO.getUsername() == null || registerDTO.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username é obrigatório");
        }
        if (registerDTO.getPassword() == null || registerDTO.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }
        if (registerDTO.getRole() == null || registerDTO.getRole().trim().isEmpty()) {
            throw new IllegalArgumentException("Role é obrigatória");
        }

        // Verificar se username já existe
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new IllegalArgumentException("Username '" + registerDTO.getUsername() + "' já existe");
        }

        // Converter DTO para entidade
        User user = userMapper.toEntityFromRegisterDTO(registerDTO);
        
        // Criptografar senha
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));

        // Se foi fornecido establishmentId, associar
        if (registerDTO.getEstablishmentId() != null) {
            Establishment establishment = establishmentRepository.findById(registerDTO.getEstablishmentId())
                .orElseThrow(() -> new IllegalArgumentException("Estabelecimento não encontrado"));
            user.setEstablishment(establishment);
        }

        // Salvar usuário
        User savedUser = userRepository.save(user);

        // Retornar DTO seguro
        return userMapper.toAuthResponseDTO(savedUser);
    }

    public UserAuthResponseDTO validateLogin(LoginDTO loginDTO) {
        // Validações básicas
        if (loginDTO == null) {
            throw new IllegalArgumentException("Dados de login não podem ser nulos");
        }
        if (loginDTO.getUsername() == null || loginDTO.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username é obrigatório");
        }
        if (loginDTO.getPassword() == null || loginDTO.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }

        // Buscar usuário por username
        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // Validar senha
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Senha inválida");
        }

        // Retornar dados seguros
        return userMapper.toAuthResponseDTO(user);
    }

    public List<UserDTO> findAll() {
        List<User> users = userRepository.findAll();
        return userMapper.toDTOList(users);
    }

    public Optional<UserDTO> findById(Long id) {
        if (id == null) return Optional.empty();
        
        return userRepository.findById(id)
                .map(userMapper::toDTO);
    }

    @Transactional
    public UserDTO update(Long id, UserDTO userDTO) {
        if (id == null || userDTO == null) {
            throw new IllegalArgumentException("ID e DTO não podem ser nulos");
        }

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com id: " + id));

        userMapper.updateEntityFromDTO(existingUser, userDTO);
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDTO(updatedUser);
    }

    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuário não encontrado com id: " + id);
        }
        
        userRepository.deleteById(id);
    }

    public Optional<User> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByUsername(username);
    }
    
    public boolean isUsernameAvailable(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return !userRepository.existsByUsername(username);
    }
}