package br.com.acougue.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "tb_order_items") // PADRONIZAÇÃO: tb_ + plural
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	@ManyToOne
	@JoinColumn(name = "order_id")
	@JsonIgnore
	private Order order;

	private Integer quantity;
	
	@Column(name = "weight_in_grams")
	private Integer weightInGrams;

	@Column(precision = 10, scale = 2)
	private BigDecimal price;

	public OrderItem() {}

	public OrderItem(Long id, Product product, Order order, Integer quantity, Integer weightInGrams, BigDecimal price) {
		this.id = id;
		this.product = product;
		this.order = order;
		this.quantity = quantity;
		this.weightInGrams = weightInGrams;
		this.price = price;
	}

	// Getters e Setters
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Product getProduct() { return product; }
	public void setProduct(Product product) { this.product = product; }
	public Order getOrder() { return order; }
	public void setOrder(Order order) { this.order = order; }
	public Integer getQuantity() { return quantity; }
	public void setQuantity(Integer quantity) { this.quantity = quantity; }
	public Integer getWeightInGrams() { return weightInGrams; }
	public void setWeightInGrams(Integer weightInGrams) { this.weightInGrams = weightInGrams; }
	public BigDecimal getPrice() { return price; }
	public void setPrice(BigDecimal price) { this.price = price; }

	@Override
	public int hashCode() { return Objects.hash(id); }

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		OrderItem other = (OrderItem) obj;
		return Objects.equals(id, other.id);
	}
}
