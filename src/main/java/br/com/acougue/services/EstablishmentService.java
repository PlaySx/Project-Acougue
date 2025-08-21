package br.com.acougue.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.acougue.dto.EstablishmentDTO;
import br.com.acougue.dto.EstablishmentRegisterDTO;
import br.com.acougue.dto.EstablishmentAuthResponseDTO;
import br.com.acougue.dto.EstablishmentCreateRequestDTO;
import br.com.acougue.entities.Establishment;
import br.com.acougue.entities.User;
import br.com.acougue.globalException.EstablishmentNaoEncontradoException;
import br.com.acougue.mapper.EstablishmentMapper;
import br.com.acougue.repository.EstablishmentRepository;
import br.com.acougue.repository.UserRepository;

@Service
public class EstablishmentService {

    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EstablishmentMapper establishmentMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * ✅ IMPLEMENTADO: Registra estabelecimento com autenticação
     */
    @Transactional
    public EstablishmentAuthResponseDTO registerWithAuth(EstablishmentRegisterDTO registerDTO) {
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
        if (registerDTO.getName() == null || registerDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do estabelecimento é obrigatório");
        }

        // Verificar se username já existe
        if (establishmentRepository.existsByUsername(registerDTO.getUsername())) {
            throw new IllegalArgumentException("Username '" + registerDTO.getUsername() + "' já existe");
        }

        // Criar estabelecimento
        Establishment establishment = new Establishment();
        establishment.setName(registerDTO.getName());
        establishment.setCnpj(registerDTO.getCnpj());
        establishment.setAddress(registerDTO.getAddress());
        establishment.setUsername(registerDTO.getUsername());
        establishment.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        establishment.setRole("ROLE_ESTABLISHMENT");

        // Salvar estabelecimento
        Establishment savedEstablishment = establishmentRepository.save(establishment);

        // Retornar resposta de autenticação
        return new EstablishmentAuthResponseDTO(
            savedEstablishment.getId(),
            savedEstablishment.getUsername(),
            savedEstablishment.getName(),
            savedEstablishment.getCnpj(),
            savedEstablishment.getAddress()
        );
    }

    /**
     * ✅ NOVO: Cria estabelecimento para um usuário específico
     */
    @Transactional
    public EstablishmentDTO createForUser(Long userId, EstablishmentCreateRequestDTO requestDTO) {
        if (userId == null || requestDTO == null) {
            throw new IllegalArgumentException("User ID e dados não podem ser nulos");
        }

        // Buscar usuário
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com id: " + userId));

        // Verificar se usuário já tem estabelecimento (se regra de negócio for 1:1)
        if (user.getEstablishment() != null) {
            throw new IllegalArgumentException("Usuário já possui um estabelecimento");
        }

        // Criar estabelecimento
        Establishment establishment = new Establishment();
        establishment.setName(requestDTO.getName());
        establishment.setCnpj(requestDTO.getCnpj());
        establishment.setAddress(requestDTO.getAddress());

        // Salvar estabelecimento
        Establishment savedEstablishment = establishmentRepository.save(establishment);

        // Associar usuário ao estabelecimento
        user.setEstablishment(savedEstablishment);
        userRepository.save(user);

        return establishmentMapper.toDTO(savedEstablishment);
    }

    /**
     * ✅ NOVO: Busca estabelecimentos de um usuário
     */
    public List<EstablishmentDTO> findByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID não pode ser nulo");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (user.getEstablishment() != null) {
            return List.of(establishmentMapper.toDTO(user.getEstablishment()));
        }
        
        return List.of(); // Lista vazia se não tem estabelecimento
    }

    @Transactional
    public EstablishmentDTO create(EstablishmentDTO establishmentDTO) {
        if (establishmentDTO == null) {
            throw new IllegalArgumentException("DTO não pode ser nulo");
        }
        
        Establishment establishment = establishmentMapper.toEntity(establishmentDTO);
        Establishment savedEstablishment = establishmentRepository.save(establishment);
        return establishmentMapper.toDTO(savedEstablishment);
    }

    public List<EstablishmentDTO> findAll() {
        List<Establishment> establishments = establishmentRepository.findAll();
        return establishmentMapper.toDTOList(establishments);
    }

    public Optional<EstablishmentDTO> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return establishmentRepository.findById(id)
                .map(establishmentMapper::toDTO);
    }

    @Transactional
    public EstablishmentDTO update(Long id, EstablishmentDTO establishmentDTO) {
        if (id == null || establishmentDTO == null) {
            throw new IllegalArgumentException("ID e DTO não podem ser nulos");
        }

        Establishment existingEstablishment = establishmentRepository.findById(id)
                .orElseThrow(() -> new EstablishmentNaoEncontradoException("Estabelecimento não encontrado com id: " + id));

        establishmentMapper.updateEntityFromDTO(existingEstablishment, establishmentDTO);
        Establishment updatedEstablishment = establishmentRepository.save(existingEstablishment);
        return establishmentMapper.toDTO(updatedEstablishment);
    }

    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        
        if (!establishmentRepository.existsById(id)) {
            throw new EstablishmentNaoEncontradoException("Estabelecimento não encontrado com id: " + id);
        }
        establishmentRepository.deleteById(id);
    }
}