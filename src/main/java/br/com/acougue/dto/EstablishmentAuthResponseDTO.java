package br.com.acougue.dto;

/**
 * DTO de resposta para operações de autenticação
 * Retorna dados seguros do estabelecimento (sem senha)
 */
public class EstablishmentAuthResponseDTO {
    
    private Long id;
    private String username;
    private String name;
    private Long cnpj;
    private String address;

    // Construtores
    public EstablishmentAuthResponseDTO() {}

    public EstablishmentAuthResponseDTO(Long id, String username, String name, Long cnpj, String address) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.cnpj = cnpj;
        this.address = address;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
        return "EstablishmentAuthResponseDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", cnpj='" + cnpj + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}