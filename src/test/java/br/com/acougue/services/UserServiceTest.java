package br.com.acougue.services;

import br.com.acougue.dto.UserDTO;
import br.com.acougue.dto.UserRegisterDTO;
import br.com.acougue.entities.Establishment;
import br.com.acougue.dto.LoginDTO;
import br.com.acougue.entities.User;
import br.com.acougue.enums.Role;
import br.com.acougue.exceptions.ResourceNotFoundException;
import br.com.acougue.mapper.UserMapper;
import br.com.acougue.repository.EstablishmentRepository;
import br.com.acougue.repository.UserRepository;
import br.com.acougue.dto.UserAuthResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EstablishmentRepository establishmentRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserRegisterDTO userRegisterDTO;
    private Establishment establishment;

    @BeforeEach
    void setUp() {
        establishment = new Establishment();
        establishment.setId(1L);
        establishment.setName("Açougue Teste");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setRole(Role.ROLE_EMPLOYEE);
        user.setEstablishment(establishment);

        userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setUsername("newuser");
        userRegisterDTO.setPassword("password123");
        userRegisterDTO.setRole("ROLE_EMPLOYEE");
        userRegisterDTO.setEstablishmentId(1L);
    }

    @Test
    void registerUser_shouldRegisterSuccessfully_whenDataIsValid() {
        // Arrange
        when(userRepository.existsByUsername(userRegisterDTO.getUsername())).thenReturn(false);
        when(establishmentRepository.findById(userRegisterDTO.getEstablishmentId())).thenReturn(Optional.of(establishment));
        when(passwordEncoder.encode(userRegisterDTO.getPassword())).thenReturn("encodedPassword");
        when(userMapper.toEntityFromRegisterDTO(userRegisterDTO)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.registerUser(userRegisterDTO);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    void registerUser_shouldThrowDataIntegrityViolationException_whenUsernameAlreadyExists() {
        // Arrange
        when(userRepository.existsByUsername(userRegisterDTO.getUsername())).thenReturn(true);

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            userService.registerUser(userRegisterDTO);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_shouldThrowResourceNotFoundException_whenEstablishmentDoesNotExist() {
        // Arrange
        when(userRepository.existsByUsername(userRegisterDTO.getUsername())).thenReturn(false);
        when(establishmentRepository.findById(userRegisterDTO.getEstablishmentId())).thenReturn(Optional.empty());
        when(userMapper.toEntityFromRegisterDTO(userRegisterDTO)).thenReturn(user); // Precisa mockar o mapper antes
        when(passwordEncoder.encode(userRegisterDTO.getPassword())).thenReturn("encodedPassword");

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.registerUser(userRegisterDTO);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findById_shouldReturnUserDTO_whenUserExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(new UserDTO());

        // Act
        UserDTO result = userService.findById(1L);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void findById_shouldThrowResourceNotFoundException_whenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.findById(99L);
        });
    }

    @Test
    void delete_shouldCallDeleteById_whenUserExists() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L); // doNothing é usado para métodos void

        // Act
        userService.delete(1L);

        // Assert
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_shouldThrowResourceNotFoundException_whenUserDoesNotExist() {
        // Arrange
        when(userRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.delete(99L);
        });

        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void findAll_shouldReturnListOfUserDTOs() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDTOList(anyList())).thenReturn(List.of(new UserDTO()));

        // Act
        List<UserDTO> result = userService.findAll();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void update_shouldReturnUpdatedUserDTO_whenUserExists() {
        // Arrange
        UserDTO userUpdateDTO = new UserDTO();
        userUpdateDTO.setUsername("updateduser");
        userUpdateDTO.setRole("ROLE_OWNER");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userUpdateDTO);

        // Act
        UserDTO result = userService.update(1L, userUpdateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("updateduser", result.getUsername());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).updateEntityFromDTO(user, userUpdateDTO);
    }

    @Test
    void update_shouldThrowResourceNotFoundException_whenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.update(99L, new UserDTO());
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void validateLogin_shouldReturnAuthResponseDTO_whenCredentialsAreValid() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("testuser", "password123");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(userMapper.toAuthResponseDTO(user)).thenReturn(new UserAuthResponseDTO());

        // Act
        UserAuthResponseDTO result = userService.validateLogin(loginDTO);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
    }

    @Test
    void validateLogin_shouldThrowBadCredentialsException_whenUserNotFound() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("nonexistent", "password123");
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            userService.validateLogin(loginDTO);
        });
    }

    @Test
    void validateLogin_shouldThrowBadCredentialsException_whenPasswordIsInvalid() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("testuser", "wrongpassword");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            userService.validateLogin(loginDTO);
        });
    }
}