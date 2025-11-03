package br.com.acougue.dto;

public class UserAuthResponseDTO {
    
    private Long id;
    private String username;
    private String role;
    private Long establishmentId;
    private String establishmentName;

    // Construtores
    public UserAuthResponseDTO() {}

    public UserAuthResponseDTO(Long id, String username, String role, Long establishmentId, String establishmentName) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.establishmentId = establishmentId;
        this.establishmentName = establishmentName;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Long getEstablishmentId() { return establishmentId; }
    public void setEstablishmentId(Long establishmentId) { this.establishmentId = establishmentId; }

    public String getEstablishmentName() { return establishmentName; }
    public void setEstablishmentName(String establishmentName) { this.establishmentName = establishmentName; }

    @Override
    public String toString() {
        return "UserAuthResponseDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", establishmentId=" + establishmentId +
                ", establishmentName='" + establishmentName + '\'' +
                '}';
    }
}