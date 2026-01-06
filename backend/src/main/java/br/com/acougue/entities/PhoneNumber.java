package br.com.acougue.entities;

import br.com.acougue.enums.PhoneType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tb_phone_numbers")
public class PhoneNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PhoneType type;
    
    @Column(name = "is_primary")
    private boolean primary; // Campo restaurado

    @ManyToOne
    @JoinColumn(name = "client_id")
    @JsonIgnore
    private Client client;

    public PhoneNumber() {}

    public PhoneNumber(String number, PhoneType type, boolean primary) {
        this.number = number;
        this.type = type;
        this.primary = primary;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public PhoneType getType() { return type; }
    public void setType(PhoneType type) { this.type = type; }
    public boolean isPrimary() { return primary; }
    public void setPrimary(boolean primary) { this.primary = primary; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PhoneNumber other = (PhoneNumber) obj;
        return Objects.equals(id, other.id);
    }
}
