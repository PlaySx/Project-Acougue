package br.com.acougue.globalException;

public class EstablishmentNaoEncontradoException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public EstablishmentNaoEncontradoException (String mensagem) {
		super(mensagem);
	}
}
