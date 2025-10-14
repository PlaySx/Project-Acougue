package br.com.acougue.enums;

public enum OrderStatus {
	
	PENDENTE("Pendente"),
	CONFIRMADO("Confirmado"),
	EM_PREPARO("Em Preparo"),
	PRONTO("Pronto para Retirada/Entrega"),
	A_CAMINHO("A Caminho"),
	ENTREGUE("Entregue"),
	CANCELADO("Cancelado");

	    private final String displayName;

	    OrderStatus(String displayName) {
	        this.displayName = displayName;
	    }

	    public String getDisplayName() {
	        return displayName;
	    }

	    @Override
	    public String toString() {
	        return displayName;
	    }
	}