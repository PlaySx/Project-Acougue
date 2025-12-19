package br.com.acougue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ClientRequestDTO {

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, message = "O nome deve ter no mínimo 3 caracteres")
    private String name;

    @NotNull(message = "O número de telefone é obrigatório")
    private Long numberPhone;

    @NotBlank(message = "O endereço é obrigatório")
    private String address;

    private String addressNeighborhood;
    private String observation;

    @NotNull(message = "O ID do estabelecimento é obrigatório")
    private Long establishmentId;

    // Getters e Setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getNumberPhone() {
        return numberPhone;
    }
    public void setNumberPhone(Long numberPhone) {
        this.numberPhone = numberPhone;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getAddressNeighborhood() {
        return addressNeighborhood;
    }
    public void setAddressNeighborhood(String addressNeighborhood) {
        this.addressNeighborhood = addressNeighborhood;
    }
    public String getObservation() {
        return observation;
    }
    public void setObservation(String observation) {
        this.observation = observation;
    }
    public Long getEstablishmentId() {
        return establishmentId;
    }
    public void setEstablishmentId(Long establishmentId) {
        this.establishmentId = establishmentId;
    }
}