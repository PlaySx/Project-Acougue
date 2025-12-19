package br.com.acougue.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.acougue.enums.OrderStatus;
import jakarta.persistence.*;

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
	private String observation;

	@ManyToOne
	@JoinColumn(name = "cliente_id")
	private Client client;

	@ManyToOne
	@JoinColumn(name = "estabelecimento_id")
	@JsonIgnore
	private Establishment establishment;

	// A relação agora é com OrderItem
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> items = new ArrayList<>();

	@Column(name = "total_value", precision = 10, scale = 2)
    private BigDecimal totalValue;

	public Order() {}

	// Getters e Setters
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public LocalDateTime getDatahora() { return datahora; }
	public void setDatahora(LocalDateTime datahora) { this.datahora = datahora; }
	public OrderStatus getStatus() { return status; }
	public void setStatus(OrderStatus status) { this.status = status; }
	public String getPaymentMethod() { return paymentMethod; }
	public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
	public String getObservation() { return observation; }
	public void setObservation(String observation) { this.observation = observation; }
	public Client getClient() { return client; }
	public void setClient(Client client) { this.client = client; }
	public Establishment getEstablishment() { return establishment; }
	public void setEstablishment(Establishment establishment) { this.establishment = establishment; }
	public List<OrderItem> getItems() { return items; }
	public void setItems(List<OrderItem> items) { this.items = items; }
    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

    // Método utilitário para adicionar itens e manter a consistência
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

	@Override
	public int hashCode() { return Objects.hash(id); }

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Order other = (Order) obj;
		return Objects.equals(id, other.id);
	}

    @Override
    public String toString() {
        return "Order{" + "id=" + id + ", datahora=" + datahora + ", status=" + status + '}';
    }
}
