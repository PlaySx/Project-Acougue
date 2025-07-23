package br.com.acougue.entities;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_Client_List")
public class Client {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private Long numberPhone;
	private String address;
	private String addressNeighborhood;
	private String Observation;
	@ManyToMany
	@JoinTable(name = "client_products", joinColumns = @JoinColumn(name = "client_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
	private List<Products> products;

	public Client() {

	}

	public Client(Long id, String name, Long numberPhone, String address, String addressNeighborhood,
			String observation, List<Products> products) {
		super();
		this.id = id;
		this.name = name;
		this.numberPhone = numberPhone;
		this.address = address;
		this.addressNeighborhood = addressNeighborhood;
		Observation = observation;
		this.products = products;
	}

	public Long getId() {
		return id;
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

	@Override
	public int hashCode() {
		return Objects.hash(Observation, address, addressNeighborhood, id, name, numberPhone, products);
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
				&& Objects.equals(addressNeighborhood, other.addressNeighborhood) && Objects.equals(id, other.id)
				&& Objects.equals(name, other.name) && Objects.equals(numberPhone, other.numberPhone)
				&& Objects.equals(products, other.products);
	}

}