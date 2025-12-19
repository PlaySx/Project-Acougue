package br.com.acougue.mapper;

import br.com.acougue.dto.OrderItemRequestDTO;
import br.com.acougue.dto.OrderItemResponseDTO;
import br.com.acougue.dto.OrderRequestDTO;
import br.com.acougue.dto.OrderResponseDTO;
import br.com.acougue.entities.*;
import br.com.acougue.enums.PricingType;
import br.com.acougue.repository.ClientRepository;
import br.com.acougue.repository.EstablishmentRepository;
import br.com.acougue.repository.ProductRepository;
import br.com.acougue.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    private final ClientRepository clientRepository;
    private final EstablishmentRepository establishmentRepository;
    private final ProductRepository productRepository;

    public OrderMapper(ClientRepository clientRepository, EstablishmentRepository establishmentRepository, ProductRepository productRepository) {
        this.clientRepository = clientRepository;
        this.establishmentRepository = establishmentRepository;
        this.productRepository = productRepository;
    }

    public Order toEntity(OrderRequestDTO dto) {
        if (dto == null) return null;

        Order order = new Order();
        order.setDatahora(LocalDateTime.now());
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setObservation(dto.getObservation());

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com id: " + dto.getClientId()));
        order.setClient(client);

        Establishment establishment = establishmentRepository.findById(dto.getEstablishmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado com id: " + dto.getEstablishmentId()));
        order.setEstablishment(establishment);

        BigDecimal totalOrderValue = BigDecimal.ZERO;

        for (var itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + itemDto.getProductId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            
            BigDecimal itemPrice;

            if (product.getPricingType() == PricingType.PER_KG) {
                if (itemDto.getWeightInGrams() == null || itemDto.getWeightInGrams() <= 0) {
                    throw new IllegalArgumentException("Peso em gramas é obrigatório para produtos por KG.");
                }
                orderItem.setWeightInGrams(itemDto.getWeightInGrams());
                BigDecimal weightInKg = new BigDecimal(itemDto.getWeightInGrams()).divide(new BigDecimal(1000), 4, RoundingMode.HALF_UP);
                itemPrice = product.getUnitPrice().multiply(weightInKg).setScale(2, RoundingMode.HALF_UP);
            } else { // PER_UNIT
                if (itemDto.getQuantity() == null || itemDto.getQuantity() <= 0) {
                    throw new IllegalArgumentException("Quantidade é obrigatória para produtos por unidade.");
                }
                orderItem.setQuantity(itemDto.getQuantity());
                itemPrice = product.getUnitPrice().multiply(new BigDecimal(itemDto.getQuantity())).setScale(2, RoundingMode.HALF_UP);
            }
            
            orderItem.setPrice(itemPrice);
            order.addItem(orderItem);
            totalOrderValue = totalOrderValue.add(itemPrice);
        }
        
        order.setTotalValue(totalOrderValue);
        return order;
    }

    public OrderResponseDTO toResponseDTO(Order entity) {
        if (entity == null) return null;

        List<OrderItemResponseDTO> itemDTOs = entity.getItems().stream()
                .map(item -> new OrderItemResponseDTO(
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
                entity.getClient() != null ? entity.getClient().getName() : null,
                entity.getEstablishment() != null ? entity.getEstablishment().getName() : null,
                itemDTOs,
                entity.getTotalValue()
        );
    }

    public List<OrderResponseDTO> toResponseDTOList(List<Order> entities) {
        return entities.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}
