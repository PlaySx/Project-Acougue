package br.com.acougue.dto;

/**
 * DTO para registro de novos estabelecimentos
 * Contém apenas os campos necessários para cadastro com autenticação
 */
public class EstablishmentRegisterDTO {
    
    private String username;
    private String password;
    private String name;
    private Long cnpj;
    private String address;

    // Construtores
    public EstablishmentRegisterDTO() {}

    public EstablishmentRegisterDTO(String username, String password, String name, Long cnpj, String address) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.cnpj = cnpj;
        this.address = address;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCnpj() {
        return cnpj;
    }

    public void setCnpj(Long cnpj) {
        this.cnpj = cnpj;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "EstablishmentRegisterDTO{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", cnpj='" + cnpj + '\'' +
                ", address='" + address + '\'' +
                '}'; // Não inclui password no toString por segurança
    }
}