package br.com.acougue.dto;

public class EstablishmentCreateRequestDTO {
    
    private String name;
    private Long cnpj;
    private String address;

    // Construtores
    public EstablishmentCreateRequestDTO() {}

    public EstablishmentCreateRequestDTO(String name, Long cnpj, String address) {
        this.name = name;
        this.cnpj = cnpj;
        this.address = address;
    }

    // Getters e Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getCnpj() { return cnpj; }
    public void setCnpj(Long cnpj) { this.cnpj = cnpj; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return "EstablishmentCreateRequestDTO{" +
                "name='" + name + '\'' +
                ", cnpj=" + cnpj +
                ", address='" + address + '\'' +
                '}';
    }
}