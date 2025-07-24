package br.com.acougue.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_Products")
public class Products {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String description;
	private Double value;
	
	@ManyToMany(mappedBy = "products")
	private List<Order> order = new ArrayList<>();
	
	@ManyToOne
	@JoinColumn(name = "establishment_id")
	private Establishment establishment;
	
	public Products() {
		
	}

	public Products(Long id, String name, String description, Double value, List<Order> order,
			Establishment establishment) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.value = value;
		this.order = order;
		this.establishment = establishment;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public List<Order> getOrder() {
		return order;
	}

	public void setOrder(List<Order> order) {
		this.order = order;
	}

	public Establishment getEstablishment() {
		return establishment;
	}

	public void setEstablishment(Establishment establishment) {
		this.establishment = establishment;
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, establishment, id, name, order, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Products other = (Products) obj;
		return Objects.equals(description, other.description) && Objects.equals(establishment, other.establishment)
				&& Objects.equals(id, other.id) && Objects.equals(name, other.name)
				&& Objects.equals(order, other.order) && Objects.equals(value, other.value);
	}

	
}