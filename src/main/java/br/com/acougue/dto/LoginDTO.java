package br.com.acougue.dto;

/**
 * DTO para autenticação de estabelecimentos
 * Contém apenas username e password para login
 */
public class LoginDTO {

    private String username;
    private String password;

    // Construtores
    public LoginDTO() {}

    public LoginDTO(String username, String password) {
        this.username = username;
        this.password = password;
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

    @Override
    public String toString() {
        return "LoginDTO{" +
                "username='" + username + '\'' +
                '}'; // Não inclui password no toString por segurança
    }
}