package br.com.acougue.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegisterDTO {

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    private String password;

    @NotBlank(message = "Role é obrigatória")
    private String role;

    // Campos opcionais para o cadastro de um proprietário com seu estabelecimento
    private String establishmentName;
    private Long cnpj;
    private String establishmentAddress;

    // Campo para associar um funcionário a um estabelecimento existente
    private Long establishmentId;

    // Getters e Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEstablishmentName() { return establishmentName; }
    public void setEstablishmentName(String establishmentName) { this.establishmentName = establishmentName; }

    public Long getCnpj() { return cnpj; }
    public void setCnpj(Long cnpj) { this.cnpj = cnpj; }

    public String getEstablishmentAddress() { return establishmentAddress; }
    public void setEstablishmentAddress(String establishmentAddress) { this.establishmentAddress = establishmentAddress; }

    public Long getEstablishmentId() { return establishmentId; }
    public void setEstablishmentId(Long establishmentId) { this.establishmentId = establishmentId; }
}