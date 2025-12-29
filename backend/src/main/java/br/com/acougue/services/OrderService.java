package br.com.acougue.services;

import br.com.acougue.dto.OrderRequestDTO;
import br.com.acougue.dto.OrderResponseDTO;
import br.com.acougue.entities.Order;
import br.com.acougue.entities.Product;
import br.com.acougue.enums.OrderStatus;
import br.com.acougue.enums.PricingType;
import br.com.acougue.exceptions.InsufficientStockException;
import br.com.acougue.exceptions.ResourceNotFoundException;
import br.com.acougue.mapper.OrderMapper;
import br.com.acougue.repository.OrderRepository;
import br.com.acougue.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository; // Adicionado para acesso ao produto
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public OrderResponseDTO create(OrderRequestDTO requestDTO) {
        // Validação de estoque e baixa
        for (var itemDto : requestDTO.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + itemDto.getProductId()));

            int requestedAmount = (product.getPricingType() == PricingType.PER_KG) ? itemDto.getWeightInGrams() : itemDto.getQuantity();
            
            if (product.getStockQuantity() < requestedAmount) {
                throw new InsufficientStockException("Estoque insuficiente para o produto: " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - requestedAmount);
            productRepository.save(product);
        }

        Order order = orderMapper.toEntity(requestDTO);
        order.setStatus(OrderStatus.PENDENTE);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponseDTO(savedOrder);
    }

    public List<OrderResponseDTO> search(Long establishmentId, String clientName, OrderStatus status, LocalDate date) {
        List<Order> orders;
        if (date != null) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            orders = orderRepository.findByEstablishmentIdAndDatahoraBetween(establishmentId, startOfDay, endOfDay);
        } else {
            orders = orderRepository.findByEstablishmentId(establishmentId);
        }

        if (clientName != null && !clientName.isEmpty()) {
            orders = orders.stream()
                .filter(order -> order.getClient() != null && order.getClient().getName().toLowerCase().contains(clientName.toLowerCase()))
                .toList();
        }

        if (status != null) {
            orders = orders.stream()
                .filter(order -> order.getStatus() == status)
                .toList();
        }

        return orderMapper.toResponseDTOList(orders);
    }

    public OrderResponseDTO findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com o ID: " + id));
        return orderMapper.toResponseDTO(order);
    }

    @Transactional
    public OrderResponseDTO updateStatus(Long id, OrderStatus newStatus) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com o ID: " + id));
        existingOrder.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(existingOrder);
        return orderMapper.toResponseDTO(updatedOrder);
    }

    @Transactional
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pedido não encontrado com o ID: " + id);
        }
        orderRepository.deleteById(id);
    }
}
