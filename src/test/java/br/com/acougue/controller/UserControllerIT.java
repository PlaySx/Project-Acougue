package br.com.acougue.controller;

import br.com.acougue.dto.UserDTO;
import br.com.acougue.dto.UserRegisterDTO;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerIT {

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

    private Long existingUserId;
    private Long nonExistingUserId = 999L;
    private Long existingEstablishmentId;
    private User existingUser;

    @BeforeEach
    void setUp() {
        // Cria um estabelecimento de teste
        Establishment establishment = new Establishment("Açougue Teste", 12345678901234L, "Rua Teste, 123");
        establishment.setUsername("acougue_teste_user");
        establishment.setPassword("senha123");
        Establishment savedEstablishment = establishmentRepository.save(establishment);
        existingEstablishmentId = savedEstablishment.getId();

        // Cria um usuário de teste
        existingUser = new User();
        existingUser.setUsername("existinguser");
        existingUser.setPassword(passwordEncoder.encode("password123"));
        existingUser.setRole(Role.ROLE_EMPLOYEE);
        existingUser.setEstablishment(savedEstablishment);
        userRepository.save(existingUser);
        existingUserId = existingUser.getId();
    }

    @Test
    void register_shouldReturnCreated_whenDataIsValid() throws Exception {
        // Arrange
        UserRegisterDTO newUserDTO = new UserRegisterDTO();
        newUserDTO.setUsername("newuser");
        newUserDTO.setPassword("newpassword123");
        newUserDTO.setRole("ROLE_EMPLOYEE");
        newUserDTO.setEstablishmentId(existingEstablishmentId);

        String jsonBody = objectMapper.writeValueAsString(newUserDTO);

        // Act
        ResultActions result = mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody));

        // Assert
        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.username").value("newuser"));
        result.andExpect(jsonPath("$.role").value("ROLE_EMPLOYEE"));
    }

    @Test
    void register_shouldReturnConflict_whenUsernameAlreadyExists() throws Exception {
        // Arrange
        UserRegisterDTO duplicateUserDTO = new UserRegisterDTO();
        duplicateUserDTO.setUsername("existinguser"); // Username que já existe
        duplicateUserDTO.setPassword("anypassword");
        duplicateUserDTO.setRole("ROLE_EMPLOYEE");

        String jsonBody = objectMapper.writeValueAsString(duplicateUserDTO);

        // Act
        ResultActions result = mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody));

        // Assert
        result.andExpect(status().isConflict());
        result.andExpect(jsonPath("$.error").value("Violação de dados"));
    }

    @Test
    void register_shouldReturnBadRequest_whenPasswordIsTooShort() throws Exception {
        // Arrange
        UserRegisterDTO invalidUserDTO = new UserRegisterDTO();
        invalidUserDTO.setUsername("anotheruser");
        invalidUserDTO.setPassword("123"); // Senha curta
        invalidUserDTO.setRole("ROLE_EMPLOYEE");

        String jsonBody = objectMapper.writeValueAsString(invalidUserDTO);

        // Act
        ResultActions result = mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody));

        // Assert
        result.andExpect(status().isBadRequest());
        result.andExpect(jsonPath("$.error").value("Erro de validação"));
        result.andExpect(jsonPath("$.errors[0].fieldName").value("password"));
        result.andExpect(jsonPath("$.errors[0].message").value("A senha deve ter no mínimo 6 caracteres"));
    }

    @Test
    @WithMockUser // Simula um usuário logado para acessar o endpoint
    void findById_shouldReturnOk_whenUserExists() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(get("/users/{id}", existingUserId)
                .accept(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(existingUserId));
        result.andExpect(jsonPath("$.username").value("existinguser"));
    }

    @Test
    @WithMockUser
    void findById_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(get("/users/{id}", nonExistingUserId)
                .accept(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void findAll_shouldReturnOkAndListOfUsers() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(get("/users")
                .accept(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$").isArray());
        result.andExpect(jsonPath("$.length()").value(1));
        result.andExpect(jsonPath("$[0].username").value("existinguser"));
    }

    @Test
    @WithMockUser
    void update_shouldReturnOk_whenDataIsValid() throws Exception {
        // Arrange
        UserDTO updatedDTO = new UserDTO();
        updatedDTO.setUsername("updateduser");
        updatedDTO.setRole("ROLE_OWNER");

        String jsonBody = objectMapper.writeValueAsString(updatedDTO);

        // Act
        ResultActions result = mockMvc.perform(put("/users/{id}", existingUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody));

        // Assert
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.username").value("updateduser"));
        result.andExpect(jsonPath("$.role").value("ROLE_OWNER"));
    }

    @Test
    @WithMockUser
    void delete_shouldReturnNoContent_whenUserExists() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(delete("/users/{id}", existingUserId));

        // Assert
        result.andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void delete_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(delete("/users/{id}", nonExistingUserId));

        // Assert
        result.andExpect(status().isNotFound());
    }

    @Test
    void findById_shouldReturnForbidden_whenUserIsAnonymous() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(get("/users/{id}", existingUserId)
                .accept(MediaType.APPLICATION_JSON));

        // Assert
        // Como o endpoint /users/** é protegido, um usuário anônimo deve receber 403 Forbidden
        result.andExpect(status().isForbidden());
    }
}