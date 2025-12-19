package br.com.acougue.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.acougue.dto.LoginDTO;
import br.com.acougue.dto.UserAuthResponseDTO;
import br.com.acougue.dto.UserRegisterDTO;
import br.com.acougue.entities.User;
import br.com.acougue.enums.Role;
import br.com.acougue.mapper.UserMapper;
import br.com.acougue.repository.EstablishmentRepository;
import br.com.acougue.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EstablishmentRepository establishmentRepository; // Mantido caso a lógica futura precise dele

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private LoginDTO loginDTO;
    private UserRegisterDTO registerDTO;

    @BeforeEach
    void setUp() {
        // Objeto User para simular o que vem do banco
        user = new User(1L, "test@example.com", "encodedPassword", Role.ROLE_EMPLOYEE, null);

        // DTO para o teste de login
        loginDTO = new LoginDTO("test@example.com", "password123");

        // DTO para o teste de registro
        registerDTO = new UserRegisterDTO();
        registerDTO.setEmail("newuser@example.com");
        registerDTO.setPassword("newpassword");
        registerDTO.setRole("ROLE_EMPLOYEE");
    }

    @Test
    void validateLogin_deveRetornarUserAuthResponseDTO_quandoCredenciaisSaoValidas() {
        // Arrange
        when(userRepository.findByEmail(loginDTO.getLogin())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())).thenReturn(true);
        when(userMapper.toAuthResponseDTO(user)).thenReturn(new UserAuthResponseDTO(user.getId(), user.getEmail(), user.getRole().name(), null, null));

        // Act
        UserAuthResponseDTO result = userService.validateLogin(loginDTO);

        // Assert
        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository).findByEmail(loginDTO.getLogin());
        verify(passwordEncoder).matches(loginDTO.getPassword(), user.getPassword());
    }

    @Test
    void validateLogin_deveLancarExcecao_quandoEmailNaoExiste() {
        // Arrange
        when(userRepository.findByEmail(loginDTO.getLogin())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            userService.validateLogin(loginDTO);
        });
    }

    @Test
    void validateLogin_deveLancarExcecao_quandoSenhaEstaIncorreta() {
        // Arrange
        when(userRepository.findByEmail(loginDTO.getLogin())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())).thenReturn(false);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            userService.validateLogin(loginDTO);
        });
    }

    @Test
    void registerUser_deveCriarNovoUsuario_comSucesso() {
        // Arrange
        User newUser = new User(2L, registerDTO.getEmail(), "encodedNewPassword", Role.ROLE_EMPLOYEE, null);
        when(userRepository.existsByEmail(registerDTO.getEmail())).thenReturn(false);
        when(userMapper.toEntityFromRegisterDTO(registerDTO)).thenReturn(new User()); // Retorna uma instância vazia para ser preenchida
        when(passwordEncoder.encode(registerDTO.getPassword())).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        when(userMapper.toAuthResponseDTO(newUser)).thenReturn(new UserAuthResponseDTO(newUser.getId(), newUser.getEmail(), newUser.getRole().name(), null, null));

        // Act
        UserAuthResponseDTO result = userService.registerUser(registerDTO);

        // Assert
        assertNotNull(result);
        assertEquals(registerDTO.getEmail(), result.getEmail());
        verify(userRepository).save(any(User.class));
    }
}
