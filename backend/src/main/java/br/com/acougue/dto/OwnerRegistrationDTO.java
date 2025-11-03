package br.com.acougue.dto;

public class OwnerRegistrationDTO {

	// Dados do usuário
    private String username;
    private String password;
    
    // Dados do estabelecimento
    private String establishmentName;
    private Long cnpj;
    private String establishmentAddress;

    // Construtores
    public OwnerRegistrationDTO() {}

    public OwnerRegistrationDTO(String username, String password, String establishmentName, 
                               Long cnpj, String establishmentAddress) {
        this.username = username;
        this.password = password;
        this.establishmentName = establishmentName;
        this.cnpj = cnpj;
        this.establishmentAddress = establishmentAddress;
    }

    // Getters e Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEstablishmentName() {
        return establishmentName;
    }

    public void setEstablishmentName(String establishmentName) {
        this.establishmentName = establishmentName;
    }

    public Long getCnpj() {
        return cnpj;
    }

    public void setCnpj(Long cnpj) {
        this.cnpj = cnpj;
    }

    public String getEstablishmentAddress() {
        return establishmentAddress;
    }

    public void setEstablishmentAddress(String establishmentAddress) {
        this.establishmentAddress = establishmentAddress;
    }

    @Override
    public String toString() {
        return "OwnerRegistrationDTO{" +
                "username='" + username + '\'' +
                ", establishmentName='" + establishmentName + '\'' +
                ", cnpj=" + cnpj +
                ", establishmentAddress='" + establishmentAddress + '\'' +
                '}'; // Não inclui password por segurança
    }

}
