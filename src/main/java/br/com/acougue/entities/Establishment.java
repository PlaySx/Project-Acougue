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
	
	@Column(unique = true, nullable = false)
	private String username;
	
	@JsonIgnore
	@Column(nullable = false)
	private String password;
	
	@Column(name = "role")
	private String role = "ROLE_ESTABLISHMENT";

	@OneToMany(mappedBy = "establishment", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<User> users = new ArrayList<>();
	
	@OneToMany(mappedBy = "establishment", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Client> clients = new ArrayList<>();

	@OneToMany(mappedBy = "establishment", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Product> products = new ArrayList<>(); // Changed from Products

	@OneToMany(mappedBy = "establishment")
	@JsonIgnore
	private List<Order> orders = new ArrayList<>();
	

	// Construtores
	public Establishment() {}

	public Establishment(String name, Long cnpj, String address) {
        this.name = name;
        this.cnpj = cnpj;
        this.address = address;
    }

	public Establishment(String username, String password, String name, Long cnpj, String address) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.cnpj = cnpj;
        this.address = address;
        this.role = "ROLE_ESTABLISHMENT";
    }

    // Getters e Setters
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
    
    public void setAddress(String address) { 
        this.address = address; 
    }

    public String getUsername() { 
        return username; 
    }
    
    public void setUsername(String username) { 
        this.username = username; 
    }

    public String getPassword() { 
        return password; 
    }
    
    public void setPassword(String password) { 
        this.password = password; 
    }

    public String getRole() { 
        return role; 
    }
    
    public void setRole(String role) { 
        this.role = role; 
    }

    public List<User> getUsers() { 
        return users; 
    }
    
    public void setUsers(List<User> users) { 
        this.users = users; 
    }

    public List<Client> getClients() { 
        return clients; 
    }
    
    public void setClients(List<Client> clients) { 
        this.clients = clients; 
    }

    public List<Product> getProducts() { // Changed from Products
        return products; 
    }
    
    public void setProducts(List<Product> products) { // Changed from Products
        this.products = products; 
    }

    public List<Order> getOrders() { 
        return orders; 
    }
    
    public void setOrders(List<Order> orders) { 
        this.orders = orders; 
    }

    @Override
    public int hashCode() {
        return Objects.hash(cnpj, id, name, username);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Establishment other = (Establishment) obj;
        return Objects.equals(cnpj, other.cnpj) && 
               Objects.equals(id, other.id) && 
               Objects.equals(name, other.name) &&
               Objects.equals(username, other.username);
    }

    @Override
    public String toString() {
        return "Establishment{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cnpj=" + cnpj +
                ", address='" + address + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}