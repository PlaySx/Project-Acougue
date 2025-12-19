package br.com.acougue.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class OrderItemRequestDTO {

    @NotNull(message = "O ID do produto é obrigatório")
    private Long productId;

    // Apenas um destes será usado, dependendo do tipo de produto
    @Positive(message = "O peso deve ser um valor positivo")
    private Integer weightInGrams;

    @Positive(message = "A quantidade deve ser um valor positivo")
    private Integer quantity;

    // Getters e Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getWeightInGrams() { return weightInGrams; }
    public void setWeightInGrams(Integer weightInGrams) { this.weightInGrams = weightInGrams; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
