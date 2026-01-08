package br.com.acougue.dto;

public class ClientSummaryDTO {
    private Long id;
    private String name;
    private String primaryPhone;
    private String address;
    private String addressNeighborhood;

    public ClientSummaryDTO(Long id, String name, String primaryPhone, String address, String addressNeighborhood) {
        this.id = id;
        this.name = name;
        this.primaryPhone = primaryPhone;
        this.address = address;
        this.addressNeighborhood = addressNeighborhood;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPrimaryPhone() { return primaryPhone; }
    public void setPrimaryPhone(String primaryPhone) { this.primaryPhone = primaryPhone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getAddressNeighborhood() { return addressNeighborhood; }
    public void setAddressNeighborhood(String addressNeighborhood) { this.addressNeighborhood = addressNeighborhood; }
}
