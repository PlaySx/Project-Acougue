package br.com.acougue.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tb_establishment")
public class Establishment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String cnpj; // CORREÇÃO: Alterado de Long para String

    private String address;

    // Campos para login do estabelecimento (opcional, dependendo da regra de negócio)
    private String username;
    private String password;

    @OneToMany(mappedBy = "establishment")
    @JsonIgnore
    private List<User> users;

    @OneToMany(mappedBy = "establishment")
    @JsonIgnore
    private List<Client> clients;

    @OneToMany(mappedBy = "establishment")
    @JsonIgnore
    private List<Product> products;

    @OneToMany(mappedBy = "establishment")
    @JsonIgnore
    private List<Order> orders;

    public Establishment() {}

    public Establishment(Long id, String name, String cnpj, String address) {
        this.id = id;
        this.name = name;
        this.cnpj = cnpj;
        this.address = address;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCnpj() { return cnpj; } // Getter retorna String
    public void setCnpj(String cnpj) { this.cnpj = cnpj; } // Setter aceita String

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }

    public List<Client> getClients() { return clients; }
    public void setClients(List<Client> clients) { this.clients = clients; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }

    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) { this.orders = orders; }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Establishment other = (Establishment) obj;
        return Objects.equals(id, other.id);
    }
}
