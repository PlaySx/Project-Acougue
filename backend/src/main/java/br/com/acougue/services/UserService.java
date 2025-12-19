package br.com.acougue.services;

import br.com.acougue.dto.LoginDTO;
import br.com.acougue.dto.UserAuthResponseDTO;
import br.com.acougue.dto.UserDTO;
import br.com.acougue.dto.UserRegisterDTO;
import br.com.acougue.entities.Establishment;
import br.com.acougue.entities.User;
import br.com.acougue.enums.Role;
import br.com.acougue.exceptions.ResourceNotFoundException;
import br.com.acougue.mapper.UserMapper;
import br.com.acougue.repository.EstablishmentRepository;
import br.com.acougue.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new DataIntegrityViolationException("Email '" + registerDTO.getEmail() + "' já está em uso.");
        }

        User user = userMapper.toEntityFromRegisterDTO(registerDTO);
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));

        // Lógica para Proprietário (ROLE_OWNER)
        if (Role.valueOf(registerDTO.getRole()) == Role.ROLE_OWNER) {
            if (registerDTO.getEstablishmentName() == null || registerDTO.getCnpj() == null) {
                throw new IllegalArgumentException("Nome do estabelecimento e CNPJ são obrigatórios para proprietários.");
            }
            if (establishmentRepository.existsByCnpj(registerDTO.getCnpj())) {
                throw new DataIntegrityViolationException("CNPJ '" + registerDTO.getCnpj() + "' já está em uso.");
            }
            
            Establishment newEstablishment = new Establishment();
            newEstablishment.setName(registerDTO.getEstablishmentName());
            newEstablishment.setCnpj(registerDTO.getCnpj());
            newEstablishment.setAddress(registerDTO.getEstablishmentAddress());
            // O username/password do estabelecimento pode ser o mesmo do usuário ou diferente
            newEstablishment.setUsername(registerDTO.getEmail()); 
            newEstablishment.setPassword(user.getPassword());

            Establishment savedEstablishment = establishmentRepository.save(newEstablishment);
            user.setEstablishment(savedEstablishment);
        } 
        // Lógica para Funcionário (ROLE_EMPLOYEE)
        else if (Role.valueOf(registerDTO.getRole()) == Role.ROLE_EMPLOYEE) {
            if (registerDTO.getEstablishmentId() == null) {
                throw new IllegalArgumentException("ID do estabelecimento é obrigatório para funcionários.");
            }
            Establishment establishment = establishmentRepository.findById(registerDTO.getEstablishmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado com o ID: " + registerDTO.getEstablishmentId()));
            user.setEstablishment(establishment);
        }

        User savedUser = userRepository.save(user);
        return userMapper.toAuthResponseDTO(savedUser);
    }

    public UserAuthResponseDTO validateLogin(LoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getLogin())
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Credenciais inválidas");
        }

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