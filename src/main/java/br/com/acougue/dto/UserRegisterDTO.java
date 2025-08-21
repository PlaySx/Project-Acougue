package br.com.acougue.dto;

public class UserRegisterDTO {
    
    private String username;
    private String password;
    private String role; // "ROLE_OWNER" ou "ROLE_EMPLOYEE"
    private Long establishmentId; // Opcional, pode ser setado depois

    // Construtores
    public UserRegisterDTO() {}

    public UserRegisterDTO(String username, String password, String role, Long establishmentId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.establishmentId = establishmentId;
    }

    // Getters e Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Long getEstablishmentId() { return establishmentId; }
    public void setEstablishmentId(Long establishmentId) { this.establishmentId = establishmentId; }

    @Override
    public String toString() {
        return "UserRegisterDTO{" +
                "username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", establishmentId=" + establishmentId +
                '}'; // Não inclui password por segurança
    }
}