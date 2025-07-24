package br.com.acougue.globalException;

public class OrderNaoEncontradoException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public OrderNaoEncontradoException(Long id) {
		super("Pedido com id " + id + " não encontrado.");
	}
}
