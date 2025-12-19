package br.com.acougue.dto;

public class UserAuthResponseDTO {
    
    private Long id;
    private String email; // Renamed from username
    private String role;
    private Long establishmentId;
    private String establishmentName;

    // Construtores
    public UserAuthResponseDTO() {}

    public UserAuthResponseDTO(Long id, String email, String role, Long establishmentId, String establishmentName) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.establishmentId = establishmentId;
        this.establishmentName = establishmentName;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; } // Renamed from getUsername
    public void setEmail(String email) { this.email = email; } // Renamed from setUsername

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
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", establishmentId=" + establishmentId +
                ", establishmentName='" + establishmentName + '\'' +
                '}';
    }
}