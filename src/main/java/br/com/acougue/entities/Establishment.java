package br.com.acougue.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;

public class Establishment {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private Long Cnpj;

	@OneToMany(mappedBy = "establishment", cascade = CascadeType.ALL)
	private List<Client> Client = new ArrayList<Client>();

	@OneToMany(mappedBy = "establishment", cascade = CascadeType.ALL)
	private List<Products> products = new ArrayList<Products>();

	public Establishment(Long id, String name, Long cnpj, List<br.com.acougue.entities.Client> client,
			List<Products> products) {
		super();
		this.id = id;
		this.name = name;
		Cnpj = cnpj;
		Client = client;
		this.products = products;
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
		return Cnpj;
	}

	public void setCnpj(Long cnpj) {
		Cnpj = cnpj;
	}

	public List<Client> getClient() {
		return Client;
	}

	public void setClient(List<Client> client) {
		Client = client;
	}

	public List<Products> getProducts() {
		return products;
	}

	public void setProducts(List<Products> products) {
		this.products = products;
	}

	@Override
	public int hashCode() {
		return Objects.hash(Client, Cnpj, id, name, products);
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
		return Objects.equals(Client, other.Client) && Objects.equals(Cnpj, other.Cnpj) && Objects.equals(id, other.id)
				&& Objects.equals(name, other.name) && Objects.equals(products, other.products);
	}

}