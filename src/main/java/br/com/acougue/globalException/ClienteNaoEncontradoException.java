package br.com.acougue.globalException;
	
	public class ClienteNaoEncontradoException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public ClienteNaoEncontradoException(String mensagem) {
	        super(mensagem);
	    }
	}
