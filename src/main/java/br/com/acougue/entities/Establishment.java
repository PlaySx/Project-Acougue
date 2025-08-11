package br.com.acougue.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
public class Establishment implements UserDetails {

	private static final long serialVersionUID = 1L;

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

	public Establishment(Long id, String name, Long cnpj, String address, String username, String password, String role,
			List<Client> clients, List<Products> products, List<Order> orders) {
		super();
		this.id = id;
		this.name = name;
		this.cnpj = cnpj;
		this.address = address;
		this.username = username;
		this.password = password;
		this.role = role;
		this.clients = clients;
		this.products = products;
		this.orders = orders;
	}

	public Long getId() {
        return id;
    } // retorna id

    public void setId(Long id) {
        this.id = id;
    } // seta id

    public String getName() {
        return name;
    } // retorna nome

    public void setName(String name) {
        this.name = name;
    } // seta nome

    public Long getCnpj() {
        return cnpj;
    }

    public void setCnpj(Long cnpj) {
		this.cnpj = cnpj;
	}

	public String getAddress() {
        return address;
    } // retorna endereço

    public void setAddress(String address) {
        this.address = address;
    } // seta endereço

    public String getUsername() {
        return username;
    } // método exigido por UserDetails (retorna o "username" usado no login)

    public void setUsername(String username) {
        this.username = username;
    } // seta username

    public String getPassword() {
        return password;
    } // método exigido por UserDetails (retorna a senha criptografada)

    public void setPassword(String password) {
        this.password = password;
    } // seta senha (no cadastro, você DEVE setar a senha já encondigada)

    public String getRole() {
        return role;
    } // retorna role

    public void setRole(String role) {
        this.role = role;
    } // seta role

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
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority(role));
	}
    
	@Override
	@JsonIgnore
	public boolean isAccountNonExpired() {
		// Se você tiver lógica de expiração de conta, aplique aqui.
        // Retornando true por enquanto (conta não expirada).
		return true;
	}
	
	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() {
		// Se quiser bloquear conta após tentativas fracassadas, aplique aqui.
		return true;
	}
	
	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
        // Se desejar expirar credenciais (senha), aplique aqui.
		return true;
	}
	
	@Override
	@JsonIgnore
	public boolean isEnabled() {
		 // Se quiser habilitar/desabilitar estabelecimento, troque para um campo booleano (ex: enabled).
		return true;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(cnpj, id, name, orders, products, username);
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
                && Objects.equals(orders, other.orders) && Objects.equals(products, other.products)
                && Objects.equals(username, other.username);
    }
	
	}