package br.com.acougue.globalException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
	
		//Trata qualquer exceção genérica	
		@ExceptionHandler(Exception.class)
		public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request){
			return new ResponseEntity<>("Erro interno" + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		//Trata qualquer exceção de Runtime
		public ResponseEntity<Object> handleRunTimeException(RuntimeException ex, WebRequest request){
			return new ResponseEntity<>("Erro de execução: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
		//Trata exceção de Id não encontrado
		public ResponseEntity<Object> handleClienteNaoEncontrado(ClienteNaoEncontradoException ex, WebRequest request){
			return new ResponseEntity<>("Cliente não encontrado: " + ex.getMessage(), HttpStatus.NOT_FOUND);
		}
		
		public ResponseEntity<Object> handleClienteNaoEncontrado(ProductNaoEncontradoException ex, WebRequest request){
			return new ResponseEntity<>("Cliente não encontrado: " + ex.getMessage(), HttpStatus.NOT_FOUND);
		}
		
		public ResponseEntity<Object> handleEstablishmentNaoEncontrado(EstablishmentNaoEncontradoException ex, WebRequest request){
			return new ResponseEntity<>("Estabelecimento não encontrado: " + ex.getMessage(), HttpStatus.NOT_FOUND);
		}

}
