package br.com.acougue.services;

import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.acougue.exceptions.ResourceNotFoundException;
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

    private final UserRepository userRepository;
    private final EstablishmentRepository establishmentRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, EstablishmentRepository establishmentRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.establishmentRepository = establishmentRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserAuthResponseDTO registerUser(UserRegisterDTO registerDTO) {
        // Verificar se username já existe
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new DataIntegrityViolationException("Username '" + registerDTO.getUsername() + "' já está em uso.");
        }

        // Converter DTO para entidade
        User user = userMapper.toEntityFromRegisterDTO(registerDTO);
        
        // Criptografar senha
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));

        // Se foi fornecido establishmentId, associar
        if (registerDTO.getEstablishmentId() != null) {
            Establishment establishment = establishmentRepository.findById(registerDTO.getEstablishmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado com o ID: " + registerDTO.getEstablishmentId()));
            user.setEstablishment(establishment);
        }

        // Salvar usuário
        User savedUser = userRepository.save(user);

        // Retornar DTO seguro
        return userMapper.toAuthResponseDTO(savedUser);
    }

    public UserAuthResponseDTO validateLogin(LoginDTO loginDTO) {
        // Buscar usuário por username
        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas"));

        // Validar senha
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Credenciais inválidas");
        }

        // Retornar dados seguros
        return userMapper.toAuthResponseDTO(user);
    }

    public List<UserDTO> findAll() {
        List<User> users = userRepository.findAll();
        return userMapper.toDTOList(users);
    }

    public UserDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));
        return userMapper.toDTO(user);
    }

    @Transactional
    public UserDTO update(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));

        userMapper.updateEntityFromDTO(existingUser, userDTO);
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDTO(updatedUser);
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado com o ID: " + id);
        }
        
        userRepository.deleteById(id);
    }
}