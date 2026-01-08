package br.com.acougue.dto;

public class ClientSummaryDTO {
    private Long id;
    private String name;
    private String primaryPhone;

    public ClientSummaryDTO(Long id, String name, String primaryPhone) {
        this.id = id;
        this.name = name;
        this.primaryPhone = primaryPhone;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPrimaryPhone() { return primaryPhone; }
    public void setPrimaryPhone(String primaryPhone) { this.primaryPhone = primaryPhone; }
}
