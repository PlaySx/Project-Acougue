package br.com.acougue.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_client")
public class Client {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "client_name")
	private String name;

	@Column(name = "number_phone")
	private Long numberPhone;

	@Column(name = "client_address")
	private String address;

	@Column(name = "address_neighborhood")
	private String addressNeighborhood;

	@Column(name = "client_observation")
	private String Observation;
	@ManyToMany
	@JoinTable(name = "client_products", joinColumns = @JoinColumn(name = "client_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
	@JsonIgnore
	private List<Products> products;

	@OneToMany(mappedBy = "client")
	@JsonIgnore
	private List<Order> orders = new ArrayList<>();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "establishment_id")
	@JsonIgnoreProperties({ "clients", "products", "orders" })
	private Establishment establishment;

	public Client() {

	}

	public Client(Long id, String name, Long numberPhone, String address, String addressNeighborhood,
			String observation, List<Products> products, List<Order> orders, Establishment establishment) {
		super();
		this.id = id;
		this.name = name;
		this.numberPhone = numberPhone;
		this.address = address;
		this.addressNeighborhood = addressNeighborhood;
		Observation = observation;
		this.products = products;
		this.orders = orders;
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

	public Long getNumberPhone() {
		return numberPhone;
	}

	public void setNumberPhone(Long numberPhone) {
		this.numberPhone = numberPhone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddressNeighborhood() {
		return addressNeighborhood;
	}

	public void setAddressNeighborhood(String addressNeighborhood) {
		this.addressNeighborhood = addressNeighborhood;
	}

	public String getObservation() {
		return Observation;
	}

	public void setObservation(String observation) {
		Observation = observation;
	}

	public List<Products> getProducts() {
		return products;
	}

	public void setProducts(List<Products> products) {
		this.products = products;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public Establishment getEstablishment() {
		return establishment;
	}

	public void setEstablishment(Establishment establishment) {
		this.establishment = establishment;
	}

	@Override
	public int hashCode() {
		return Objects.hash(Observation, address, addressNeighborhood, establishment, id, name, numberPhone, orders,
				products);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Client other = (Client) obj;
		return Objects.equals(Observation, other.Observation) && Objects.equals(address, other.address)
				&& Objects.equals(addressNeighborhood, other.addressNeighborhood)
				&& Objects.equals(establishment, other.establishment) && Objects.equals(id, other.id)
				&& Objects.equals(name, other.name) && Objects.equals(numberPhone, other.numberPhone)
				&& Objects.equals(orders, other.orders) && Objects.equals(products, other.products);
	}

}