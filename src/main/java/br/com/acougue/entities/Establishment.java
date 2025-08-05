package br.com.acougue.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_establishment")
public class Establishment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "establishment_name")
	private String name;
	
	@Column(name = "cnpj")
	private Long cnpj;
	
	@Column(name = "establishment_address")
	private String address;

	//Agora funciona porque Client tem 'establishment'
	@OneToMany(mappedBy = "establishment", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Client> clients = new ArrayList<>();  // Mudei nome para 'clients'

	//Agora funciona porque Products tem 'establishment'
	@OneToMany(mappedBy = "establishment", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Products> products = new ArrayList<>();

	//Nome da propriedade em Order.java é 'establishment'
	@OneToMany(mappedBy = "establishment")
	@JsonIgnore
	private List<Order> orders = new ArrayList<>();  // Mudei nome para 'orders'

	public Establishment() {}

	public Establishment(Long id, String name, Long cnpj, List<Client> clients,
			List<Products> products, List<Order> orders) {
		this.id = id;
		this.name = name;
		this.cnpj = cnpj;
		this.clients = clients;  // ✅ ATUALIZADO
		this.products = products;
		this.orders = orders;    // ✅ ATUALIZADO
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

	public Long getCnpj() {
		return cnpj;
	}

	public void setCnpj(Long cnpj) {
		this.cnpj = cnpj;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String adress) {
		this.address = adress;
	}

	public List<Client> getClients() {
		return clients;
	}

	public void setClients(List<Client> clients) {
		this.clients = clients;
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Establishment other = (Establishment) obj;
		return Objects.equals(cnpj, other.cnpj) && Objects.equals(clients, other.clients)
				&& Objects.equals(id, other.id) && Objects.equals(name, other.name)
				&& Objects.equals(orders, other.orders) && Objects.equals(products, other.products);
	}	
	}