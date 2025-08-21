package br.com.acougue.enums;

public enum OrderStatus {
	
	    PENDENTE("Pendente"),
	    EM_PREPARO("Em Preparo"),
	    PRONTO("Pronto"),
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