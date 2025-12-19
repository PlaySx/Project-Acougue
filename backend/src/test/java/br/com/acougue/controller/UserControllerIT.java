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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        userRepository.deleteAll();
        establishmentRepository.deleteAll();

        Establishment establishment = new Establishment("Açougue Teste", 12345678901234L, "Rua Teste, 123");
        establishment.setUsername("acougue_teste_user");
        establishment.setPassword(passwordEncoder.encode("senha123"));
        Establishment savedEstablishment = establishmentRepository.save(establishment);
        existingEstablishmentId = savedEstablishment.getId();

        existingUser = new User();
        existingUser.setEmail("existing@example.com");
        existingUser.setPassword(passwordEncoder.encode("password123"));
        existingUser.setRole(Role.ROLE_EMPLOYEE);
        existingUser.setEstablishment(savedEstablishment);
        userRepository.save(existingUser);
        existingUserId = existingUser.getId();
    }

    // Seus testes de registro foram movidos para o AuthController, então estes podem ser removidos ou adaptados
    // Por enquanto, vou comentá-los para focar nos endpoints de /users

    @Test
    @WithMockUser(username = "admin", roles = {"OWNER"}) // Simula um usuário com permissão
    void findById_shouldReturnOk_whenUserExists() throws Exception {
        ResultActions result = mockMvc.perform(get("/users/{id}", existingUserId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(existingUserId));
        result.andExpect(jsonPath("$.email").value("existing@example.com"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"OWNER"})
    void findById_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        ResultActions result = mockMvc.perform(get("/users/{id}", nonExistingUserId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"OWNER"})
    void findAll_shouldReturnOkAndListOfUsers() throws Exception {
        ResultActions result = mockMvc.perform(get("/users")
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$").isArray());
        result.andExpect(jsonPath("$.length()").value(1));
        result.andExpect(jsonPath("$[0].email").value("existing@example.com"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"OWNER"})
    void update_shouldReturnOk_whenDataIsValid() throws Exception {
        UserDTO updatedDTO = new UserDTO();
        updatedDTO.setEmail("updated@example.com"); // O email não deve ser atualizado, mas o DTO precisa do campo
        updatedDTO.setRole("ROLE_OWNER");

        String jsonBody = objectMapper.writeValueAsString(updatedDTO);

        ResultActions result = mockMvc.perform(put("/users/{id}", existingUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.email").value("existing@example.com")); // Email não muda
        result.andExpect(jsonPath("$.role").value("ROLE_OWNER"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"OWNER"})
    void delete_shouldReturnNoContent_whenUserExists() throws Exception {
        ResultActions result = mockMvc.perform(delete("/users/{id}", existingUserId));

        result.andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"OWNER"})
    void delete_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        ResultActions result = mockMvc.perform(delete("/users/{id}", nonExistingUserId));

        result.andExpect(status().isNotFound());
    }

    @Test
    void findById_shouldReturnForbidden_whenUserIsAnonymous() throws Exception {
        ResultActions result = mockMvc.perform(get("/users/{id}", existingUserId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }
}