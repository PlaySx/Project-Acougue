package br.com.acougue.entities;

import br.com.acougue.enums.PricingType;
import br.com.acougue.enums.ProductCategory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "tb_products")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "product_name", nullable = false)
	private String name;
	
	@Column(name = "product_description")
	private String description;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProductCategory category;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PricingType pricingType;

	@Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
	private BigDecimal unitPrice; // Preço por KG ou por Unidade
	
	@ManyToOne
	@JoinColumn(name = "establishment_id")
	@JsonIgnoreProperties({"clients", "products", "orders"})
	private Establishment establishment;
	
	public Product() {}

	// Getters e Setters
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public ProductCategory getCategory() { return category; }
	public void setCategory(ProductCategory category) { this.category = category; }
	public PricingType getPricingType() { return pricingType; }
	public void setPricingType(PricingType pricingType) { this.pricingType = pricingType; }
	public BigDecimal getUnitPrice() { return unitPrice; }
	public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
	public Establishment getEstablishment() { return establishment; }
	public void setEstablishment(Establishment establishment) { this.establishment = establishment; }

	@Override
	public int hashCode() { return Objects.hash(id); }

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Product other = (Product) obj;
		return Objects.equals(id, other.id);
	}

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", unitPrice=" + unitPrice +
                '}';
    }
}
