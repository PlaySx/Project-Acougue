package br.com.acougue.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;

@ControllerAdvice // Indica que esta classe vai tratar exceções de toda a aplicação.
public class RestExceptionHandler {

    /**
     * Trata a exceção de credenciais inválidas (lançada no AuthService).
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<StandardError> handleBadCredentials(BadCredentialsException e, HttpServletRequest request) {
        String error = "Não autorizado";
        HttpStatus status = HttpStatus.UNAUTHORIZED; // HTTP 401
        StandardError err = new StandardError();
        err.setTimestamp(Instant.now());
        err.setStatus(status.value());
        err.setError(error);
        err.setMessage(e.getMessage());
        err.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    /**
     * Trata exceções de recurso não encontrado (lançada nos serviços ao buscar por ID).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> handleResourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        String error = "Recurso não encontrado";
        HttpStatus status = HttpStatus.NOT_FOUND; // HTTP 404
        StandardError err = new StandardError();
        err.setTimestamp(Instant.now());
        err.setStatus(status.value());
        err.setError(error);
        err.setMessage(e.getMessage());
        err.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    /**
     * Trata exceções de violação de integridade do banco de dados (ex: campo unique duplicado).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> handleDataIntegrityViolation(DataIntegrityViolationException e, HttpServletRequest request) {
        String error = "Violação de dados";
        HttpStatus status = HttpStatus.CONFLICT; // HTTP 409
        StandardError err = new StandardError();
        err.setTimestamp(Instant.now());
        err.setStatus(status.value());
        err.setError(error);
        err.setMessage("Já existe um recurso com os dados informados. Verifique os campos únicos, como 'username'.");
        err.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    /**
     * Trata exceções de validação de DTOs (campos em branco, formato inválido, etc.).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        String error = "Erro de validação";
        HttpStatus status = HttpStatus.BAD_REQUEST; // HTTP 400
        ValidationError err = new ValidationError();
        err.setTimestamp(Instant.now());
        err.setStatus(status.value());
        err.setError(error);
        err.setMessage("Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente.");
        err.setPath(request.getRequestURI());

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            err.addError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(status).body(err);
    }

    /**
     * Trata qualquer outra exceção não esperada.
     * É um "fallback" para garantir que nunca exponhamos um erro feio para o cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> handleGenericException(Exception e, HttpServletRequest request) {
        String error = "Erro interno do servidor";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // HTTP 500
        StandardError err = new StandardError();
        err.setTimestamp(Instant.now());
        err.setStatus(status.value());
        err.setError(error);
        err.setMessage("Ocorreu um erro inesperado no sistema. Tente novamente."); // Mensagem genérica por segurança
        err.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }
}