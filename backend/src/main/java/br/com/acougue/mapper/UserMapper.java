package br.com.acougue.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.acougue.dto.UserDTO;
import br.com.acougue.dto.UserRegisterDTO;
import br.com.acougue.dto.UserAuthResponseDTO;
import br.com.acougue.entities.User;
import br.com.acougue.entities.Establishment;
import br.com.acougue.enums.Role;

@Component
public class UserMapper {

    // ====================== MÉTODOS PARA AUTENTICAÇÃO ======================
    
    /**
     * Converte UserRegisterDTO para User (sem senha criptografada)
     * A senha deve ser criptografada no Service
     */
    public User toEntityFromRegisterDTO(UserRegisterDTO registerDTO) {
        if (registerDTO == null) {
            return null;
        }
        
        User user = new User();
        user.setEmail(registerDTO.getEmail()); // Corrigido de getUsername
        // password será setada no Service após criptografia
        
        // Converter string role para enum
        if (registerDTO.getRole() != null) {
            user.setRole(Role.valueOf(registerDTO.getRole()));
        }
        
        // establishmentId será resolvido no Service
        
        return user;
    }
    
    /**
     * Converte User para UserAuthResponseDTO (sem senha)
     */
    public UserAuthResponseDTO toAuthResponseDTO(User user) {
        if (user == null) {
            return null;
        }
        
        UserAuthResponseDTO dto = new UserAuthResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail()); // Corrigido de getUsername
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        
        if (user.getEstablishment() != null) {
            dto.setEstablishmentId(user.getEstablishment().getId());
            dto.setEstablishmentName(user.getEstablishment().getName());
        }
        
        return dto;
    }

    // ====================== MÉTODOS PARA CRUD BÁSICO ======================
    
    /**
     * Converte User para UserDTO (sem dados sensíveis)
     */
    public UserDTO toDTO(User user) {
        if (user == null) return null;
        
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail()); // Corrigido de getUsername
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        
        if (user.getEstablishment() != null) {
            dto.setEstablishmentId(user.getEstablishment().getId());
            dto.setEstablishmentName(user.getEstablishment().getName());
        }
        
        return dto;
    }
    
    /**
     * Converte UserDTO para User (para atualizações)
     */
    public User toEntity(UserDTO dto) {
        if (dto == null) return null;
        
        User user = new User();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail()); // Corrigido de getUsername
        
        // Converter string role para enum
        if (dto.getRole() != null) {
            user.setRole(Role.valueOf(dto.getRole()));
        }
        
        // establishmentId será resolvido no Service se necessário
        
        return user;
    }
    
    /**
     * Atualiza entidade User existente com dados do DTO
     * NÃO atualiza email nem password por segurança
     */
    public void updateEntityFromDTO(User entity, UserDTO dto) {
        if (entity == null || dto == null) return;
        
        // Apenas campos editáveis (não email nem password)
        if (dto.getRole() != null) {
            entity.setRole(Role.valueOf(dto.getRole()));
        }
        
        // establishmentId será tratado no Service se necessário
    }
    
    /**
     * Converte lista de Users para lista de UserDTOs
     */
    public List<UserDTO> toDTOList(List<User> users) {
        if (users == null) return null;
        
        return users.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Converte lista de Users para lista de UserAuthResponseDTOs
     */
    public List<UserAuthResponseDTO> toAuthResponseDTOList(List<User> users) {
        if (users == null) return null;
        
        return users.stream()
            .map(this::toAuthResponseDTO)
            .collect(Collectors.toList());
    }

    // ====================== MÉTODOS AUXILIARES ======================
    
    /**
     * Cria User básico com establishment
     */
    public User createUserWithEstablishment(UserRegisterDTO registerDTO, Establishment establishment) {
        User user = toEntityFromRegisterDTO(registerDTO);
        if (user != null) {
            user.setEstablishment(establishment);
        }
        return user;
    }
    
    /**
     * Cria funcionário com establishment e role fixa
     */
    public User createEmployee(String email, String password, Establishment establishment) {
        User employee = new User();
        employee.setEmail(email); // Corrigido de setUsername
        employee.setPassword(password); // Será criptografada no Service
        employee.setRole(Role.ROLE_EMPLOYEE);
        employee.setEstablishment(establishment);
        return employee;
    }
    
    /**
     * Cria proprietário com establishment e role fixa
     */
    public User createOwner(String email, String password, Establishment establishment) {
        User owner = new User();
        owner.setEmail(email); // Corrigido de setUsername
        owner.setPassword(password); // Será criptografada no Service
        owner.setRole(Role.ROLE_OWNER);
        owner.setEstablishment(establishment);
        return owner;
    }
}