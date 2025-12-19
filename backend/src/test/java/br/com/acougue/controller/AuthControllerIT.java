package br.com.acougue.controller;

import br.com.acougue.dto.LoginDTO;
import br.com.acougue.entities.Establishment;
import br.com.acougue.entities.User;
import br.com.acougue.enums.Role;
import br.com.acougue.repository.EstablishmentRepository;
import br.com.acougue.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String plainPassword = "password123";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        establishmentRepository.deleteAll();

        User user = new User();
        user.setEmail("testuser@example.com"); // Corrigido para setEmail
        user.setPassword(passwordEncoder.encode(plainPassword));
        user.setRole(Role.ROLE_EMPLOYEE);
        userRepository.save(user);

        Establishment establishment = new Establishment();
        establishment.setUsername("testestablishment");
        establishment.setPassword(passwordEncoder.encode(plainPassword));
        establishment.setName("Açougue Teste");
        establishmentRepository.save(establishment);
    }

    @Test
    void login_shouldReturnOkAndToken_whenUserCredentialsAreValid() throws Exception {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("testuser@example.com", plainPassword); // Usa email para o login
        String jsonBody = objectMapper.writeValueAsString(loginDTO);

        // Act
        ResultActions result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody));

        // Assert
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.token").exists());
    }

    // O login de estabelecimento continua usando username, o que é ok.
    @Test
    void login_shouldReturnOkAndToken_whenEstablishmentCredentialsAreValid() throws Exception {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("testestablishment", plainPassword);
        String jsonBody = objectMapper.writeValueAsString(loginDTO);

        // Act
        ResultActions result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody));

        // Assert
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.token").exists());
    }

    @Test
    void login_shouldReturnUnauthorized_whenPasswordIsInvalid() throws Exception {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("testuser@example.com", "wrongpassword");
        String jsonBody = objectMapper.writeValueAsString(loginDTO);

        // Act
        ResultActions result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody));

        // Assert
        result.andExpect(status().isUnauthorized());
    }
}
