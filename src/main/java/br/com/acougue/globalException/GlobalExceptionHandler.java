package br.com.acougue.globalException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Object> handleRunTimeException(RuntimeException ex, WebRequest request){
		return new ResponseEntity<>("Erro de execução: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ClienteNaoEncontradoException.class)
	public ResponseEntity<Object> handleClienteNaoEncontrado(ClienteNaoEncontradoException ex, WebRequest request){
		return new ResponseEntity<>("Cliente não encontrado: " + ex.getMessage(), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(ProductNaoEncontradoException.class)
	public ResponseEntity<Object> handleProductNaoEncontrado(ProductNaoEncontradoException ex, WebRequest request){
		return new ResponseEntity<>("Produto não encontrado: " + ex.getMessage(), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(EstablishmentNaoEncontradoException.class)
	public ResponseEntity<Object> handleEstablishmentNaoEncontrado(EstablishmentNaoEncontradoException ex, WebRequest request){
		return new ResponseEntity<>("Estabelecimento não encontrado: " + ex.getMessage(), HttpStatus.NOT_FOUND);
	}
	
	// ✅ CORRIGIDO: Mudou de EstablishmentNaoEncontradoException.class para OrderNaoEncontradoException.class
	@ExceptionHandler(OrderNaoEncontradoException.class)
	public ResponseEntity<Object> handleOrderNaoEncontrado(OrderNaoEncontradoException ex, WebRequest request){
		return new ResponseEntity<>("Order não encontrado: " + ex.getMessage(), HttpStatus.NOT_FOUND);
	}
}