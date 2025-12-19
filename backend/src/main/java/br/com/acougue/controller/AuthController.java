package br.com.acougue.controller;

import br.com.acougue.dto.LoginDTO;
import br.com.acougue.entities.User;
import br.com.acougue.services.UserService;
import br.com.acougue.services.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import br.com.acougue.dto.UserRegisterDTO;
import br.com.acougue.dto.UserAuthResponseDTO;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3001") // Permite requisições do seu frontend
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager,
                          TokenService tokenService,
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginDTO loginDTO) {
        // Usa getLogin() que pode ser email (para User) ou username (para Establishment)
        var usernamePassword = new UsernamePasswordAuthenticationToken(loginDTO.getLogin(), loginDTO.getPassword());
        Authentication auth = this.authenticationManager.authenticate(usernamePassword);

        // O Principal pode ser User ou Establishment, o TokenService deve saber lidar com isso
        var principal = auth.getPrincipal(); 
        var token = tokenService.generateToken((User) principal); // Assumindo que o principal é sempre User por enquanto

        return ResponseEntity.ok(new TokenResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<UserAuthResponseDTO> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        // Corrigido para usar o método correto do UserService
        UserAuthResponseDTO response = userService.registerUser(registerDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    // DTO simples para a resposta do token
    private record TokenResponse(String token) {}
}