package br.com.acougue.controller;

import br.com.acougue.dto.LoginRequestDTO;
import br.com.acougue.dto.LoginResponseDTO;
import br.com.acougue.dto.RegisterRequestDTO;
import br.com.acougue.entities.User;
import br.com.acougue.services.RegistrationService;
import br.com.acougue.services.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RegistrationService registrationService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService, RegistrationService registrationService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.registrationService = registrationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.getLogin(), data.getPassword());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((User) auth.getPrincipal());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> register(@RequestBody @Valid RegisterRequestDTO data) {
        User newUser = registrationService.register(data);
        var token = tokenService.generateToken(newUser);
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}
