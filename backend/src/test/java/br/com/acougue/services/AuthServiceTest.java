/*
package br.com.acougue.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.acougue.dto.EstablishmentAuthResponseDTO;
import br.com.acougue.dto.LoginDTO;
import br.com.acougue.dto.UserAuthResponseDTO;
import br.com.acougue.entities.Establishment;
import br.com.acougue.entities.User;
import br.com.acougue.enums.Role;
import br.com.acougue.repository.EstablishmentRepository;
import br.com.acougue.repository.UserRepository;

@ExtendWith(MockitoExtension.class) // Habilita a integração do Mockito com o JUnit 5
class AuthServiceTest {

    // Cria um "dublê" (mock) para as dependências
    @Mock
    private UserRepository userRepository;

    @Mock
    private EstablishmentRepository establishmentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    // Cria uma instância real de AuthService e injeta os mocks acima nela
    @InjectMocks
    private AuthService authService;

    private LoginDTO loginDTO;
    private User user;
    private Establishment establishment;

    // Este método é executado antes de cada teste
    @BeforeEach
    void setUp() {
        // Prepara objetos comuns que serão usados nos testes
        loginDTO = new LoginDTO("testuser", "password123");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setRole(Role.ROLE_EMPLOYEE); // Corrigido para um valor de enum válido

        establishment = new Establishment();
        establishment.setId(1L);
        establishment.setUsername("testestablishment");
        establishment.setPassword("encodedPassword");
        establishment.setName("Açougue do Bairro");
    }

    @Test
    void authenticate_deveRetornarUser_quandoCredenciaisDeUsuarioSaoValidas() {
        // Arrange (Organizar): Prepara o cenário do teste.
        // Quando o método findByUsername for chamado com "testuser", retorne o nosso objeto 'user'.
        when(userRepository.findByUsername(loginDTO.getUsername())).thenReturn(Optional.of(user));
        // Quando o passwordEncoder for verificar a senha, retorne 'true' (senha correta).
        when(passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())).thenReturn(true);

        // Act (Agir): Executa o método que queremos testar.
        Object result = authService.authenticate(loginDTO);

        // Assert (Verificar): Confere se o resultado é o esperado.
        assertNotNull(result);
        assertTrue(result instanceof UserAuthResponseDTO, "O resultado deveria ser um UserAuthResponseDTO");
        assertEquals(user.getUsername(), ((UserAuthResponseDTO) result).getUsername());

        // Verifica se o repositório de estabelecimento NUNCA foi chamado, otimizando o fluxo.
        verify(establishmentRepository, never()).findByUsername(anyString());
    }

    @Test
    void authenticate_deveRetornarEstablishment_quandoUsuarioNaoExisteECredenciaisDeEstabelecimentoSaoValidas() {
        // Arrange
        loginDTO.setUsername("testestablishment"); // Altera o DTO para o teste de estabelecimento
        when(userRepository.findByUsername(loginDTO.getUsername())).thenReturn(Optional.empty()); // Simula usuário não encontrado
        when(establishmentRepository.findByUsername(loginDTO.getUsername())).thenReturn(Optional.of(establishment));
        when(passwordEncoder.matches(loginDTO.getPassword(), establishment.getPassword())).thenReturn(true);

        // Act
        Object result = authService.authenticate(loginDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof EstablishmentAuthResponseDTO, "O resultado deveria ser um EstablishmentAuthResponseDTO");
        assertEquals(establishment.getUsername(), ((EstablishmentAuthResponseDTO) result).getUsername());
    }

    @Test
    void authenticate_deveLancarExcecao_quandoCredenciaisSaoInvalidas() {
        // Arrange
        // Simula que o username não existe em nenhum dos dois repositórios.
        when(userRepository.findByUsername(loginDTO.getUsername())).thenReturn(Optional.empty());
        when(establishmentRepository.findByUsername(loginDTO.getUsername())).thenReturn(Optional.empty());

        // Act & Assert
        // Verifica se uma exceção do tipo IllegalArgumentException é lançada.
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.authenticate(loginDTO);
        });

        assertEquals("Credenciais inválidas", exception.getMessage());
    }

    @Test
    void authenticate_deveLancarExcecao_quandoUsuarioExisteMasSenhaEstaIncorreta() {
        // Arrange
        // Simula que o usuário foi encontrado
        when(userRepository.findByUsername(loginDTO.getUsername())).thenReturn(Optional.of(user));
        // Mas a senha não bate
        when(passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())).thenReturn(false);
        // E ele também não existe como estabelecimento (para testar o fluxo completo de falha)
        when(establishmentRepository.findByUsername(loginDTO.getUsername())).thenReturn(Optional.empty());

        // Act & Assert
        // Verifica se a exceção correta é lançada
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.authenticate(loginDTO);
        });

        assertEquals("Credenciais inválidas", exception.getMessage());
    }
}
*/