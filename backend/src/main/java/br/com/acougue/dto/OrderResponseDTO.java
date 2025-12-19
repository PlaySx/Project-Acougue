package br.com.acougue.dto;

import br.com.acougue.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDTO {

    private Long id;
    private LocalDateTime datahora;
    private OrderStatus status;
    private String paymentMethod;
    private String observation;
    private String clientName;
    private String establishmentName;
    private List<OrderItemResponseDTO> items; // Alterado de List<ProductResponseDTO>
    private BigDecimal totalValue; // Alterado de Double

    public OrderResponseDTO(Long id, LocalDateTime datahora, OrderStatus status, String paymentMethod,
                            String observation, String clientName, String establishmentName,
                            List<OrderItemResponseDTO> items, BigDecimal totalValue) {
        this.id = id;
        this.datahora = datahora;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.observation = observation;
        this.clientName = clientName;
        this.establishmentName = establishmentName;
        this.items = items;
        this.totalValue = totalValue;
    }

    // Getters
    public Long getId() { return id; }
    public LocalDateTime getDatahora() { return datahora; }
    public OrderStatus getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getObservation() { return observation; }
    public String getClientName() { return clientName; }
    public String getEstablishmentName() { return establishmentName; }
    public List<OrderItemResponseDTO> getItems() { return items; }
    public BigDecimal getTotalValue() { return totalValue; }
}