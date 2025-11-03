package br.com.acougue.controller;

import br.com.acougue.dto.ProductRequestDTO;
import br.com.acougue.entities.Establishment;
import br.com.acougue.entities.Product;
import br.com.acougue.repository.EstablishmentRepository;
import br.com.acougue.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Ativa o application-test.properties
@Transactional // Garante que cada teste rode em uma transação que será revertida ao final
class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Usado para converter objetos em JSON

    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Autowired
    private ProductRepository productRepository;

    private Long existingEstablishmentId;
    private Long existingProductId;
    private Long nonExistingProductId = 999L;

    @BeforeEach
    void setUp() {
        // Cria um estabelecimento no banco de dados H2 antes de cada teste
        Establishment establishment = new Establishment("Açougue Teste", 12345678901234L, "Rua Teste, 123");
        establishment.setUsername("acougue_teste");
        establishment.setPassword("senha123");
        Establishment savedEstablishment = establishmentRepository.save(establishment);
        existingEstablishmentId = savedEstablishment.getId();

        // Cria um produto de teste associado ao estabelecimento
        Product product = new Product();
        product.setName("Costela");
        product.setValue(35.50);
        product.setEstablishment(savedEstablishment);
        Product savedProduct = productRepository.save(product);
        existingProductId = savedProduct.getId();

        // Cria um segundo produto para testar a busca
        Product product2 = new Product();
        product2.setName("Picanha");
        product2.setValue(89.90);
        product2.setEstablishment(savedEstablishment);
        productRepository.save(product2);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"EMPLOYEE"}) // Simula um usuário logado com a role EMPLOYEE
    void create_shouldReturnCreated_whenUserIsAuthenticatedAndProductIsValid() throws Exception {
        // Arrange
        ProductRequestDTO requestDTO = new ProductRequestDTO();
        requestDTO.setName("Picanha Premium");
        requestDTO.setValue(89.90);
        requestDTO.setEstablishmentId(existingEstablishmentId);

        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        // Act
        ResultActions result = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody));

        // Assert
        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").value("Picanha Premium"));
        result.andExpect(jsonPath("$.value").value(89.90));
    }

    @Test
    void create_shouldReturnBadRequest_whenNameIsMissing() throws Exception {
        // Arrange
        ProductRequestDTO requestDTO = new ProductRequestDTO();
        // requestDTO.setName("Picanha Premium"); // Nome está faltando
        requestDTO.setValue(89.90);
        requestDTO.setEstablishmentId(existingEstablishmentId);

        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        // Act
        ResultActions result = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody));

        // Assert
        result.andExpect(status().isBadRequest());
        result.andExpect(jsonPath("$.error").value("Erro de validação"));
        result.andExpect(jsonPath("$.errors[0].fieldName").value("name"));
        result.andExpect(jsonPath("$.errors[0].message").value("Nome é obrigatório"));
    }

    @Test
    void findById_shouldReturnOk_whenProductExists() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(get("/products/{id}", existingProductId)
                .accept(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(existingProductId));
        result.andExpect(jsonPath("$.name").value("Costela"));
        result.andExpect(jsonPath("$.value").value(35.50));
    }

    @Test
    void findById_shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(get("/products/{id}", nonExistingProductId)
                .accept(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.error").value("Recurso não encontrado"));
        result.andExpect(jsonPath("$.message").value("Produto não encontrado com o ID: " + nonExistingProductId));
    }

    @Test
    void search_shouldReturnOkAndFilteredList_whenSearchingByName() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(get("/products")
                .param("name", "Picanha") // Busca por um produto específico
                .param("establishmentId", existingEstablishmentId.toString())
                .accept(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$").isArray());
        result.andExpect(jsonPath("$.length()").value(1)); // Espera encontrar apenas 1 produto
        result.andExpect(jsonPath("$[0].name").value("Picanha"));
    }

    @Test
    void search_shouldReturnBadRequest_whenEstablishmentIdIsMissing() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(get("/products")
                .param("name", "Picanha")
                // Não passa o establishmentId
                .accept(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isBadRequest());
        // O Spring lança uma exceção diferente para parâmetros de requisição faltando,
        // então a estrutura do erro pode variar. Este teste verifica o status 400.
    }

    @Test
    void update_shouldReturnOk_whenDataIsValid() throws Exception {
        // Arrange
        ProductRequestDTO requestDTO = new ProductRequestDTO();
        requestDTO.setName("Costela Gaúcha (Atualizada)");
        requestDTO.setValue(42.00);
        requestDTO.setEstablishmentId(existingEstablishmentId);

        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        // Act
        ResultActions result = mockMvc.perform(put("/products/{id}", existingProductId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody));

        // Assert
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.name").value("Costela Gaúcha (Atualizada)"));
        result.andExpect(jsonPath("$.value").value(42.00));
    }

    @Test
    void update_shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
        // Arrange
        ProductRequestDTO requestDTO = new ProductRequestDTO();
        requestDTO.setName("Produto Fantasma");
        requestDTO.setValue(10.00);
        requestDTO.setEstablishmentId(existingEstablishmentId);

        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        // Act
        ResultActions result = mockMvc.perform(put("/products/{id}", nonExistingProductId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody));

        // Assert
        result.andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldReturnNoContent_whenProductExists() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/products/{id}", existingProductId))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/products/{id}", nonExistingProductId))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldReturnForbidden_whenRequestIsAnonymous() throws Exception {
        // Arrange
        ProductRequestDTO requestDTO = new ProductRequestDTO();
        requestDTO.setName("Produto Secreto");
        requestDTO.setValue(99.99);
        requestDTO.setEstablishmentId(existingEstablishmentId);

        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        // Act
        ResultActions result = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody));

        // Assert
        // Retorna 403 Forbidden porque o Spring Security bloqueia antes de chegar na lógica de autenticação
        // que retornaria 401. Para testes de API, 403 ou 401 são aceitáveis para usuários anônimos.
        result.andExpect(status().isForbidden());
    }
}