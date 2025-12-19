package br.com.acougue.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para autenticação de usuários e estabelecimentos.
 * Usa um campo genérico 'login' que pode ser email ou username.
 */
public class LoginDTO {
    
    @NotBlank(message = "O campo de login é obrigatório")
    private String login; // Campo genérico para email ou username

    @NotBlank(message = "O campo password é obrigatório")
    private String password;

    // Construtores
    public LoginDTO() {}

    public LoginDTO(String login, String password) {
        this.login = login;
        this.password = password;
    }

    // Getters e Setters
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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
                "login='" + login + '\'' +
                '}'; // Não inclui password no toString por segurança
    }
}