package br.com.acougue.dto;

import java.math.BigDecimal;

public class OrderItemResponseDTO {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Integer weightInGrams;
    private BigDecimal price;

    public OrderItemResponseDTO(Long productId, String productName, Integer quantity, Integer weightInGrams, BigDecimal price) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.weightInGrams = weightInGrams;
        this.price = price;
    }

    // Getters
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Integer getQuantity() { return quantity; }
    public Integer getWeightInGrams() { return weightInGrams; }
    public BigDecimal getPrice() { return price; }
}