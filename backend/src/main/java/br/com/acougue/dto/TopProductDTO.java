package br.com.acougue.dto;

public class TopProductDTO {
    private String productName;
    private Long quantitySold; // Pode ser a contagem de vendas ou a soma do peso, por exemplo

    public TopProductDTO(String productName, Long quantitySold) {
        this.productName = productName;
        this.quantitySold = quantitySold;
    }

    // Getters
    public String getProductName() {
        return productName;
    }

    public Long getQuantitySold() {
        return quantitySold;
    }
}
