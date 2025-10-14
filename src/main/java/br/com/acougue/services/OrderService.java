package br.com.acougue.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.acougue.dto.OrderRequestDTO;
import br.com.acougue.dto.OrderResponseDTO;
import br.com.acougue.entities.Order;
import br.com.acougue.enums.OrderStatus;
import br.com.acougue.exceptions.ResourceNotFoundException;
import br.com.acougue.mapper.OrderMapper;
import br.com.acougue.repository.OrderRepository;

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
        // Se não foi fornecida data/hora, usar atual
        if (requestDTO.getDatahora() == null) {
            requestDTO.setDatahora(LocalDateTime.now());
        }

        Order order = orderMapper.toEntity(requestDTO);
        // Definir status inicial se não foi definido
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PENDENTE);
        }
        
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponseDTO(savedOrder);
    }

    public List<OrderResponseDTO> findAll() {
        List<Order> orders = orderRepository.findAll();
        return orderMapper.toResponseDTOList(orders);
    }

    public List<OrderResponseDTO> findByEstablishmentId(Long establishmentId) {
        List<Order> orders = orderRepository.findByEstablishmentId(establishmentId);
        return orderMapper.toResponseDTOList(orders);
    }

    public List<OrderResponseDTO> findByClientId(Long clientId) {
        List<Order> orders = orderRepository.findByClientId(clientId);
        return orderMapper.toResponseDTOList(orders);
    }

    public List<OrderResponseDTO> findByStatus(OrderStatus status, Long establishmentId) {
        List<Order> orders = orderRepository.findByStatusAndEstablishmentId(status, establishmentId);
        return orderMapper.toResponseDTOList(orders);
    }

    public List<OrderResponseDTO> findRecentOrders(Long establishmentId) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Order> orders = orderRepository.findRecentOrdersByEstablishmentId(establishmentId, thirtyDaysAgo);
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
    public OrderResponseDTO update(Long id, OrderRequestDTO requestDTO) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com o ID: " + id));

        orderMapper.updateEntityFromDTO(existingOrder, requestDTO);
        Order updatedOrder = orderRepository.save(existingOrder);
        return orderMapper.toResponseDTO(updatedOrder);
    }

    @Transactional
    public void delete(Long id) {
        Order orderToDelete = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com o ID: " + id));
        orderRepository.delete(orderToDelete);
    }
}