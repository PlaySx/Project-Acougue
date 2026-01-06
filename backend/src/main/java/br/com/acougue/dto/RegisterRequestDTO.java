package br.com.acougue.dto;

import br.com.acougue.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterRequestDTO {

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 6)
    private String password;

    @NotNull
    private Role role;

    // Campos para ROLE_OWNER
    private String establishmentName;
    private String establishmentCnpj;
    private String establishmentAddress; // Novo campo

    // Campo para ROLE_EMPLOYEE
    private Long establishmentId;

    // Getters e Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getEstablishmentName() { return establishmentName; }
    public void setEstablishmentName(String establishmentName) { this.establishmentName = establishmentName; }
    public String getEstablishmentCnpj() { return establishmentCnpj; }
    public void setEstablishmentCnpj(String establishmentCnpj) { this.establishmentCnpj = establishmentCnpj; }
    public String getEstablishmentAddress() { return establishmentAddress; }
    public void setEstablishmentAddress(String establishmentAddress) { this.establishmentAddress = establishmentAddress; }
    public Long getEstablishmentId() { return establishmentId; }
    public void setEstablishmentId(Long establishmentId) { this.establishmentId = establishmentId; }
}
