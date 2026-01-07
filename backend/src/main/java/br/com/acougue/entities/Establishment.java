package br.com.acougue.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tb_establishments")
public class Establishment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CORREÇÃO: Forçando o tipo TEXT
    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(nullable = false, unique = true)
    private String cnpj;

    // CORREÇÃO: Forçando o tipo TEXT
    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @OneToMany(mappedBy = "establishment", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "establishment", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Client> clients = new ArrayList<>();

    @OneToMany(mappedBy = "establishment", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "establishment", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Order> orders = new ArrayList<>();

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
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
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
