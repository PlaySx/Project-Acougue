package br.com.acougue.globalException;

public class ProductNaoEncontradoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProductNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

}
