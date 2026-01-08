package br.com.acougue.dto;

import br.com.acougue.enums.PricingType;
import java.math.BigDecimal;

public class ProductSummaryDTO {
    private Long id;
    private String name;
    private BigDecimal unitPrice;
    private PricingType pricingType;
    private Integer stockQuantity;

    public ProductSummaryDTO(Long id, String name, BigDecimal unitPrice, PricingType pricingType, Integer stockQuantity) {
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
        this.pricingType = pricingType;
        this.stockQuantity = stockQuantity;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public PricingType getPricingType() { return pricingType; }
    public void setPricingType(PricingType pricingType) { this.pricingType = pricingType; }
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
}
