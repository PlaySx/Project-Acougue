package br.com.acougue.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.acougue.dto.OrderRequestDTO;
import br.com.acougue.dto.OrderResponseDTO;
import br.com.acougue.entities.Order;
import br.com.acougue.enums.OrderStatus;
import br.com.acougue.mapper.OrderMapper;
import br.com.acougue.repository.OrderRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMapper orderMapper;

    @Transactional
    public OrderResponseDTO create(OrderRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("DTO não pode ser nulo");
        }
        
        // Validações básicas
        if (requestDTO.getClientId() == null) {
            throw new IllegalArgumentException("ID do cliente é obrigatório");
        }
        if (requestDTO.getEstablishmentId() == null) {
            throw new IllegalArgumentException("ID do estabelecimento é obrigatório");
        }
        if (requestDTO.getProductIds() == null || requestDTO.getProductIds().isEmpty()) {
            throw new IllegalArgumentException("Pelo menos um produto é obrigatório");
        }

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
        if (establishmentId == null) {
            throw new IllegalArgumentException("ID do estabelecimento não pode ser nulo");
        }
        
        List<Order> orders = orderRepository.findByEstablishmentId(establishmentId);
        return orderMapper.toResponseDTOList(orders);
    }

    public List<OrderResponseDTO> findByClientId(Long clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo");
        }
        
        List<Order> orders = orderRepository.findByClientId(clientId);
        return orderMapper.toResponseDTOList(orders);
    }

    public List<OrderResponseDTO> findByStatus(OrderStatus status, Long establishmentId) {
        if (status == null || establishmentId == null) {
            throw new IllegalArgumentException("Status e ID do estabelecimento não podem ser nulos");
        }
        
        List<Order> orders = orderRepository.findByStatusAndEstablishmentId(status, establishmentId);
        return orderMapper.toResponseDTOList(orders);
    }

    public List<OrderResponseDTO> findRecentOrders(Long establishmentId) {
        if (establishmentId == null) {
            throw new IllegalArgumentException("ID do estabelecimento não pode ser nulo");
        }
        
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Order> orders = orderRepository.findRecentOrdersByEstablishmentId(establishmentId, thirtyDaysAgo);
        return orderMapper.toResponseDTOList(orders);
    }

    public Optional<OrderResponseDTO> findById(Long id) {
        if (id == null) return Optional.empty();
        
        return orderRepository.findById(id)
                .map(orderMapper::toResponseDTO);
    }

    @Transactional
    public OrderResponseDTO updateStatus(Long id, OrderStatus newStatus) {
        if (id == null || newStatus == null) {
            throw new IllegalArgumentException("ID e status não podem ser nulos");
        }

        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado com id: " + id));

        existingOrder.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(existingOrder);
        return orderMapper.toResponseDTO(updatedOrder);
    }

    @Transactional
    public OrderResponseDTO update(Long id, OrderRequestDTO requestDTO) {
        if (id == null || requestDTO == null) {
            throw new IllegalArgumentException("ID e DTO não podem ser nulos");
        }

        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado com id: " + id));

        orderMapper.updateEntityFromDTO(existingOrder, requestDTO);
        Order updatedOrder = orderRepository.save(existingOrder);
        return orderMapper.toResponseDTO(updatedOrder);
    }

    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        
        if (!orderRepository.existsById(id)) {
            throw new IllegalArgumentException("Pedido não encontrado com id: " + id);
        }
        
        orderRepository.deleteById(id);
    }
}