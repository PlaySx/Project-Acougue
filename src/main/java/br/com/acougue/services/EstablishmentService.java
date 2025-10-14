package br.com.acougue.services;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.acougue.dto.EstablishmentCreateRequestDTO;
import br.com.acougue.dto.EstablishmentDTO;
import br.com.acougue.dto.EstablishmentRegisterDTO;
import br.com.acougue.dto.EstablishmentAuthResponseDTO;
import br.com.acougue.entities.Establishment;
import br.com.acougue.entities.User;
import br.com.acougue.exceptions.ResourceNotFoundException;
import br.com.acougue.mapper.EstablishmentMapper;
import br.com.acougue.repository.EstablishmentRepository;
import br.com.acougue.repository.UserRepository;

@Service
public class EstablishmentService {

    private final EstablishmentRepository establishmentRepository;
    private final UserRepository userRepository;
    private final EstablishmentMapper establishmentMapper;
    private final PasswordEncoder passwordEncoder;

    public EstablishmentService(EstablishmentRepository establishmentRepository, UserRepository userRepository, EstablishmentMapper establishmentMapper, PasswordEncoder passwordEncoder) {
        this.establishmentRepository = establishmentRepository;
        this.userRepository = userRepository;
        this.establishmentMapper = establishmentMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * ✅ IMPLEMENTADO: Registra estabelecimento com autenticação
     */
    @Transactional
    public EstablishmentAuthResponseDTO registerWithAuth(EstablishmentRegisterDTO registerDTO) {
        // Verificar se username já existe
        if (establishmentRepository.existsByUsername(registerDTO.getUsername())) {
            throw new DataIntegrityViolationException("Username '" + registerDTO.getUsername() + "' já está em uso.");
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
        // Buscar usuário
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + userId));

        // Verificar se o CNPJ já está em uso
        if (!establishmentRepository.existsById(requestDTO.getCnpj())) {// Cria a entidade Establishment a partir do DTO
            Establishment establishment = new Establishment();
            establishment.setName(requestDTO.getName());
            establishment.setCnpj(requestDTO.getCnpj());
            establishment.setAddress(requestDTO.getAddress());

            // Associa o novo estabelecimento ao usuário existente
            user.setEstablishment(establishment);
            userRepository.save(user);

            return establishmentMapper.toDTO(user.getEstablishment());
        } else {
            throw new DataIntegrityViolationException("CNPJ '" + requestDTO.getCnpj() + "' já está em uso.");
        }

    }

    /**
     * ✅ NOVO: Busca estabelecimentos de um usuário
     */
    public List<EstablishmentDTO> findByUserId(Long userId) {
        List<Establishment> establishments = establishmentRepository.findByUsers_Id(userId);
        return establishmentMapper.toDTOList(establishments);
    }

    @Transactional
    public EstablishmentDTO create(EstablishmentDTO establishmentDTO) {
        Establishment establishment = establishmentMapper.toEntity(establishmentDTO);
        Establishment savedEstablishment = establishmentRepository.save(establishment);
        return establishmentMapper.toDTO(savedEstablishment);
    }

    public List<EstablishmentDTO> findAll() {
        return establishmentMapper.toDTOList(establishmentRepository.findAll());
    }

    public EstablishmentDTO findById(Long id) {
        Establishment establishment = establishmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado com o ID: " + id));
        return establishmentMapper.toDTO(establishment);
    }

    @Transactional
    public EstablishmentDTO update(Long id, EstablishmentDTO establishmentDTO) {
        Establishment existingEstablishment = establishmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado com o ID: " + id));

        establishmentMapper.updateEntityFromDTO(existingEstablishment, establishmentDTO);
        Establishment updatedEstablishment = establishmentRepository.save(existingEstablishment);
        return establishmentMapper.toDTO(updatedEstablishment);
    }

    @Transactional
    public void delete(Long id) {
        Establishment establishmentToDelete = establishmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado com o ID: " + id));
        establishmentRepository.delete(establishmentToDelete);
    }
}