package br.com.acougue.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.acougue.enums.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_order")
public class Order {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "order_date_time")
	private LocalDateTime datahora;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "order_status")
	private OrderStatus status = OrderStatus.PENDENTE;
	
	@Column(name = "payment_method")
	private String paymentMethod;
	
	@Column(name = "order_observation")
	private String observação;
	
	@ManyToOne
	@JoinColumn(name = "cliente_id")
	private Client client;
	
	@ManyToOne
	@JoinColumn(name = "estabelecimento_id")
	@JsonIgnore
	private Establishment establishment;
	
	@ManyToMany
	@JoinTable(
			name = "order_product",
			joinColumns = @JoinColumn(name = "order_id"),
			inverseJoinColumns = @JoinColumn(name = "product_id")
			
			)
	@JsonIgnore
	private List<Products> products = new ArrayList<>();

	public Order() {
		
	}
	
	public Order(Long id, LocalDateTime datahora, OrderStatus status, String paymentMethod, String observação,
			Client client, Establishment establishment, List<Products> products) {
		super();
		this.id = id;
		this.datahora = datahora;
		this.status = status;
		this.paymentMethod = paymentMethod;
		this.observação = observação;
		this.client = client;
		this.establishment = establishment;
		this.products = products;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getDatahora() {
		return datahora;
	}

	public void setDatahora(LocalDateTime datahora) {
		this.datahora = datahora;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getObservação() {
		return observação;
	}

	public void setObservação(String observação) {
		this.observação = observação;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Establishment getEstablishment() {
		return establishment;
	}

	public void setEstablishment(Establishment establishment) {
		this.establishment = establishment;
	}

	public List<Products> getProducts() {
		return products;
	}

	public void setProducts(List<Products> products) {
		this.products = products;
	}

	@Override
	public int hashCode() {
		return Objects.hash(client, datahora, establishment, id, observação, paymentMethod, products, status);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		return Objects.equals(client, other.client) && Objects.equals(datahora, other.datahora)
				&& Objects.equals(establishment, other.establishment) && Objects.equals(id, other.id)
				&& Objects.equals(observação, other.observação) && Objects.equals(paymentMethod, other.paymentMethod)
				&& Objects.equals(products, other.products) && status == other.status;
	}
	
	
	
	
}


