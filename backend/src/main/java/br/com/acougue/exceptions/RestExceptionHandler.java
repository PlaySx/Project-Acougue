package br.com.acougue.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;

@ControllerAdvice // Indica que esta classe vai tratar exceções de toda a aplicação.
public class RestExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    /**
     * Trata a exceção de credenciais inválidas (lançada no AuthService).
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<StandardError> handleBadCredentials(BadCredentialsException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Não autorizado", e.getMessage(), request);
    }

    /**
     * Trata exceções de acesso negado (usuário autenticado sem permissão).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<StandardError> handleAccessDenied(AccessDeniedException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Acesso negado", "Você não tem permissão para acessar este recurso.", request);
    }

    /**
     * Trata exceções de recurso não encontrado (lançada nos serviços ao buscar por ID).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> handleResourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Recurso não encontrado", e.getMessage(), request);
    }

    /**
     * Trata exceções de violação de integridade do banco de dados (ex: campo unique duplicado).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> handleDataIntegrityViolation(DataIntegrityViolationException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Violação de dados", "Já existe um recurso com os dados informados. Verifique os campos únicos, como 'username'.", request);
    }

    /**
     * Trata exceções de violação de regras de negócio.
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<StandardError> handleBusinessRule(BusinessRuleException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Violação de regra de negócio", e.getMessage(), request);
    }

    /**
     * Trata exceções de validação de DTOs (campos em branco, formato inválido, etc.).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY; // HTTP 422 é mais semântico para falha de validação
        ValidationError err = buildValidationErrorResponse(status, "Erro de validação", "Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente.", request);

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            err.addError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(status).body(err);
    }
    /**
     * Trata exceções de validação de parâmetros de requisição (@RequestParam, @PathVariable).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<StandardError> handleConstraintViolation(ConstraintViolationException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // HTTP 400
        ValidationError err = buildValidationErrorResponse(status, "Erro de validação de parâmetro", "Um ou mais parâmetros da requisição são inválidos.", request);

        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            // Extrai o nome do parâmetro do path da propriedade
            String fieldName = violation.getPropertyPath().toString();
            err.addError(fieldName.substring(fieldName.lastIndexOf('.') + 1), violation.getMessage());
        }
        return ResponseEntity.status(status).body(err);
    }

    /**
     * Trata exceções de desserialização de JSON (ex: valor de enum inválido).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardError> handleHttpMessageNotReadable(HttpMessageNotReadableException e, HttpServletRequest request) {
        // Mensagem mais amigável para o usuário
        String message = "O corpo da requisição contém um valor inválido. Verifique os tipos de dados e valores permitidos.";
        if (e.getMostSpecificCause().getMessage().contains("not one of the values accepted for Enum class")) {
            message = "Um dos campos do tipo 'enum' recebeu um valor não permitido.";
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Requisição inválida", message, request);
    }

    /**
     * Trata exceções de método HTTP não suportado.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<StandardError> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String message = "O método '" + e.getMethod() + "' não é suportado para esta requisição. Métodos suportados: " + e.getSupportedHttpMethods();
        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, "Método não permitido", message, request);
    }

    /**
     * Trata qualquer outra exceção não esperada.
     * É um "fallback" para garantir que nunca exponhamos um erro feio para o cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> handleGenericException(Exception e, HttpServletRequest request) {
        logger.error("Erro inesperado no servidor: ", e); // Adiciona o log do erro completo
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor", "Ocorreu um erro inesperado no sistema. Tente novamente.", request);
    }

    /**
     * Método auxiliar para construir a resposta de erro padrão.
     */
    private ResponseEntity<StandardError> buildErrorResponse(HttpStatus status, String error, String message, HttpServletRequest request) {
        StandardError err = new StandardError();
        err.setTimestamp(Instant.now());
        err.setStatus(status.value());
        err.setError(error);
        err.setMessage(message);
        err.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    /**
     * Método auxiliar para construir a base de uma resposta de erro de validação.
     */
    private ValidationError buildValidationErrorResponse(HttpStatus status, String error, String message, HttpServletRequest request) {
        ValidationError err = new ValidationError();
        err.setTimestamp(Instant.now());
        err.setStatus(status.value());
        err.setError(error);
        err.setMessage(message);
        err.setPath(request.getRequestURI());
        return err;
    }
}