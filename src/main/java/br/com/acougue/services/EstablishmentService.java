package br.com.acougue.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.acougue.dto.EstablishmentDTO;
import br.com.acougue.dto.EstablishmentRegisterDTO;
import br.com.acougue.dto.EstablishmentAuthResponseDTO;
import br.com.acougue.dto.LoginDTO;
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

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    
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
            throw new IllegalArgumentException("Nome é obrigatório");
        }

        // Verificar se username já existe
        if (establishmentRepository.existsByUsername(registerDTO.getUsername())) {
            throw new RuntimeException("Username '" + registerDTO.getUsername() + "' já existe");
        }

        // Converter DTO para entidade
        Establishment establishment = establishmentMapper.toEntityFromRegisterDTO(registerDTO);
        
        // Criptografar e setar senha
        establishment.setPassword(passwordEncoder.encode(registerDTO.getPassword()));

        // Salvar no banco
        Establishment savedEstablishment = establishmentRepository.save(establishment);

        // Retornar DTO seguro (sem senha)
        return establishmentMapper.toAuthResponseDTO(savedEstablishment);
    }

    
    public EstablishmentAuthResponseDTO validateLogin(LoginDTO loginDTO) {
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

        // Buscar estabelecimento por username
        Establishment establishment = establishmentRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Validar senha
        if (!passwordEncoder.matches(loginDTO.getPassword(), establishment.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }

        // Retornar dados seguros (sem senha)
        return establishmentMapper.toAuthResponseDTO(establishment);
    }

    public Optional<Establishment> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        return establishmentRepository.findByUsername(username);
    }
    
    public boolean isUsernameAvailable(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return !establishmentRepository.existsByUsername(username);
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

    /**
     * Lista todos os estabelecimentos
     */
    public List<EstablishmentDTO> findAll() {
        List<Establishment> establishments = establishmentRepository.findAll();
        return establishmentMapper.toDTOList(establishments);
    }

    /**
     * Busca estabelecimento por ID e retorna DTO com contadores
     */
    public Optional<EstablishmentDTO> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return establishmentRepository.findById(id)
                .map(establishmentMapper::toDTO);
    }

    /**
     * Atualiza estabelecimento (não atualiza username nem senha)
     */
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

    /**
     * Deleta estabelecimento
     */
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