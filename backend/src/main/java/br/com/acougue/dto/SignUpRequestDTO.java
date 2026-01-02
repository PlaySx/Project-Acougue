package br.com.acougue.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignUpRequestDTO {

    @NotBlank(message = "O nome do estabelecimento é obrigatório")
    private String establishmentName;

    @NotBlank(message = "O CNPJ é obrigatório")
    private String establishmentCnpj;

    @NotBlank(message = "O email do proprietário é obrigatório")
    @Email(message = "Email inválido")
    private String ownerEmail;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    private String ownerPassword;

    // Getters e Setters
    public String getEstablishmentName() { return establishmentName; }
    public void setEstablishmentName(String establishmentName) { this.establishmentName = establishmentName; }
    public String getEstablishmentCnpj() { return establishmentCnpj; }
    public void setEstablishmentCnpj(String establishmentCnpj) { this.establishmentCnpj = establishmentCnpj; }
    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }
    public String getOwnerPassword() { return ownerPassword; }
    public void setOwnerPassword(String ownerPassword) { this.ownerPassword = ownerPassword; }
}
