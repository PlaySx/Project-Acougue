package br.com.acougue.mapper;

import br.com.acougue.dto.OrderItemResponseDTO;
import br.com.acougue.dto.OrderResponseDTO;
import br.com.acougue.entities.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderResponseDTO toResponseDTO(Order entity) {
        if (entity == null) return null;

        List<OrderItemResponseDTO> itemDTOs = entity.getItems().stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getWeightInGrams(),
                        item.getPrice()
                ))
                .collect(Collectors.toList());

        return new OrderResponseDTO(
                entity.getId(),
                entity.getDatahora(),
                entity.getStatus(),
                entity.getPaymentMethod(),
                entity.getObservation(),
                entity.getClient() != null ? entity.getClient().getId() : null, // Adiciona o clientId
                entity.getClient() != null ? entity.getClient().getName() : "Cliente não identificado",
                entity.getEstablishment() != null ? entity.getEstablishment().getName() : "Estabelecimento não identificado",
                itemDTOs,
                entity.getTotalValue()
        );
    }

    public List<OrderResponseDTO> toResponseDTOList(List<Order> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }
}
