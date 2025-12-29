package br.com.acougue.dto;

import br.com.acougue.enums.PricingType;
import br.com.acougue.enums.ProductCategory;
import java.math.BigDecimal;

public class ProductResponseDTO {

	private Long id;
	private String name;
	private String description;
	private ProductCategory category;
	private PricingType pricingType;
	private BigDecimal unitPrice;
	private Integer stockQuantity;
	private Long establishmentId;

	public ProductResponseDTO(Long id, String name, String description, ProductCategory category, PricingType pricingType, BigDecimal unitPrice, Integer stockQuantity, Long establishmentId) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.category = category;
		this.pricingType = pricingType;
		this.unitPrice = unitPrice;
		this.stockQuantity = stockQuantity;
		this.establishmentId = establishmentId;
	}

	// Getters
	public Long getId() { return id; }
	public String getName() { return name; }
	public String getDescription() { return description; }
	public ProductCategory getCategory() { return category; }
	public PricingType getPricingType() { return pricingType; }
	public BigDecimal getUnitPrice() { return unitPrice; }
	public Integer getStockQuantity() { return stockQuantity; }
	public Long getEstablishmentId() { return establishmentId; }
}
