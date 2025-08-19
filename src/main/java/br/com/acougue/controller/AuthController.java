package br.com.acougue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.acougue.dto.EstablishmentAuthResponseDTO;
import br.com.acougue.dto.EstablishmentRegisterDTO;
import br.com.acougue.dto.LoginDTO;
import br.com.acougue.services.EstablishmentService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private EstablishmentService establishmentService;

    /**
     * Endpoint para registro de estabelecimento com autenticação
     */
    @PostMapping("/register")
    public ResponseEntity<EstablishmentAuthResponseDTO> register(@RequestBody EstablishmentRegisterDTO registerDTO) {
        try {
            EstablishmentAuthResponseDTO response = establishmentService.registerWithAuth(registerDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para login de estabelecimento
     */
    @PostMapping("/login")
    public ResponseEntity<EstablishmentAuthResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        try {
            EstablishmentAuthResponseDTO response = establishmentService.validateLogin(loginDTO);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}