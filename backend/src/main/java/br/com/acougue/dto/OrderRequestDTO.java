package br.com.acougue.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class OrderRequestDTO {

    @NotNull(message = "O ID do cliente é obrigatório")
    private Long clientId;

    @NotEmpty(message = "O pedido deve ter pelo menos um item")
    private List<OrderItemRequestDTO> items;

    private String paymentMethod;
    private String observation;

    @NotNull(message = "O ID do estabelecimento é obrigatório")
    private Long establishmentId;

    // Getters e Setters
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public List<OrderItemRequestDTO> getItems() { return items; }
    public void setItems(List<OrderItemRequestDTO> items) { this.items = items; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getObservation() { return observation; }
    public void setObservation(String observation) { this.observation = observation; }

    public Long getEstablishmentId() { return establishmentId; }
    public void setEstablishmentId(Long establishmentId) { this.establishmentId = establishmentId; }
}
