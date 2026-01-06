package br.com.acougue.entities;

import br.com.acougue.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tb_orders") // PADRONIZAÇÃO: tb_ + plural
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "order_date_time")
	private LocalDateTime datahora;

	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@ManyToOne
	@JoinColumn(name = "client_id")
	@JsonIgnoreProperties("orders")
	private Client client;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<OrderItem> items = new ArrayList<>();

	@Column(name = "total_value", precision = 10, scale = 2)
	private BigDecimal totalValue;
	
	@Column(name = "payment_method")
	private String paymentMethod;
	
	@Column(name = "observation")
	private String observation;
	
	@ManyToOne
	@JoinColumn(name = "establishment_id")
	@JsonIgnoreProperties({"clients", "products", "orders"})
	private Establishment establishment;

	public Order() {}

	public Order(Long id, LocalDateTime datahora, OrderStatus status, Client client, BigDecimal totalValue, String paymentMethod, String observation, Establishment establishment) {
		this.id = id;
		this.datahora = datahora;
		this.status = status;
		this.client = client;
		this.totalValue = totalValue;
		this.paymentMethod = paymentMethod;
		this.observation = observation;
		this.establishment = establishment;
	}

	// Getters e Setters
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public LocalDateTime getDatahora() { return datahora; }
	public void setDatahora(LocalDateTime datahora) { this.datahora = datahora; }
	public OrderStatus getStatus() { return status; }
	public void setStatus(OrderStatus status) { this.status = status; }
	public Client getClient() { return client; }
	public void setClient(Client client) { this.client = client; }
	public List<OrderItem> getItems() { return items; }
	public void setItems(List<OrderItem> items) { this.items = items; }
	public BigDecimal getTotalValue() { return totalValue; }
	public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
	public String getPaymentMethod() { return paymentMethod; }
	public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
	public String getObservation() { return observation; }
	public void setObservation(String observation) { this.observation = observation; }
	public Establishment getEstablishment() { return establishment; }
	public void setEstablishment(Establishment establishment) { this.establishment = establishment; }

	@Override
	public int hashCode() { return Objects.hash(id); }

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Order other = (Order) obj;
		return Objects.equals(id, other.id);
	}
}
