package br.com.acougue.dto;

import java.math.BigDecimal;

public class OrderItemResponseDTO {

    private Long id;
    private Long productId; // NOVO CAMPO
    private String productName;
    private Integer quantity;
    private Integer weightInGrams;
    private BigDecimal price;

    // Construtor atualizado
    public OrderItemResponseDTO(Long id, Long productId, String productName, Integer quantity, Integer weightInGrams, BigDecimal price) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.weightInGrams = weightInGrams;
        this.price = price;
    }

    // Getters
    public Long getId() { return id; }
    public Long getProductId() { return productId; } // Getter novo
    public String getProductName() { return productName; }
    public Integer getQuantity() { return quantity; }
    public Integer getWeightInGrams() { return weightInGrams; }
    public BigDecimal getPrice() { return price; }
}
