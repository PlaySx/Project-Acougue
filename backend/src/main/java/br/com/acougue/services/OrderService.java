package br.com.acougue.services;

import br.com.acougue.dto.OrderRequestDTO;
import br.com.acougue.dto.OrderResponseDTO;
import br.com.acougue.entities.Order;
import br.com.acougue.enums.OrderStatus;
import br.com.acougue.exceptions.ResourceNotFoundException;
import br.com.acougue.mapper.OrderMapper;
import br.com.acougue.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public OrderResponseDTO create(OrderRequestDTO requestDTO) {
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
            // CORREÇÃO SENIOR: Chamando o método padronizado
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