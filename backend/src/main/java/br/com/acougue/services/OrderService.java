package br.com.acougue.services;

import br.com.acougue.dto.OrderItemRequestDTO;
import br.com.acougue.dto.OrderRequestDTO;
import br.com.acougue.dto.OrderResponseDTO;
import br.com.acougue.entities.Client;
import br.com.acougue.entities.Order;
import br.com.acougue.entities.OrderItem;
import br.com.acougue.entities.Product;
import br.com.acougue.enums.OrderStatus;
import br.com.acougue.exceptions.InsufficientStockException;
import br.com.acougue.exceptions.ResourceNotFoundException;
import br.com.acougue.mapper.OrderMapper;
import br.com.acougue.repository.ClientRepository;
import br.com.acougue.repository.OrderRepository;
import br.com.acougue.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, ClientRepository clientRepository, ProductRepository productRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public OrderResponseDTO create(OrderRequestDTO requestDTO) {
        Client client = clientRepository.findById(requestDTO.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o ID: " + requestDTO.getClientId()));

        Order order = new Order();
        order.setClient(client);
        order.setEstablishment(client.getEstablishment());
        order.setPaymentMethod(requestDTO.getPaymentMethod());
        order.setObservation(requestDTO.getObservation());
        order.setStatus(OrderStatus.PENDENTE);
        order.setDatahora(LocalDateTime.now());

        List<OrderItem> items = new ArrayList<>();
        BigDecimal totalValue = BigDecimal.ZERO;

        for (OrderItemRequestDTO itemDTO : requestDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + itemDTO.getProductId()));

            int quantityToDecrease = itemDTO.getWeightInGrams() != null ? itemDTO.getWeightInGrams() : itemDTO.getQuantity();
            if (product.getStockQuantity() < quantityToDecrease) {
                throw new InsufficientStockException("Estoque insuficiente para o produto: " + product.getName());
            }
            product.setStockQuantity(product.getStockQuantity() - quantityToDecrease);
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setOrder(order);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setWeightInGrams(itemDTO.getWeightInGrams());
            
            BigDecimal itemPrice = product.getUnitPrice().multiply(
                itemDTO.getWeightInGrams() != null 
                ? new BigDecimal(itemDTO.getWeightInGrams()).divide(new BigDecimal(1000)) 
                : new BigDecimal(itemDTO.getQuantity())
            );
            orderItem.setPrice(itemPrice);
            
            items.add(orderItem);
            totalValue = totalValue.add(itemPrice);
        }

        order.setItems(items);
        order.setTotalValue(totalValue);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponseDTO(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findByFilters(Long establishmentId, String clientName, OrderStatus status, LocalDate date) {
        if (date != null) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            return orderMapper.toResponseDTOList(orderRepository.findByEstablishmentIdAndDatahoraBetween(establishmentId, startOfDay, endOfDay));
        }
        if (status != null) {
            return orderMapper.toResponseDTOList(orderRepository.findByStatusAndEstablishmentId(status, establishmentId));
        }
        return orderMapper.toResponseDTOList(orderRepository.findByEstablishmentId(establishmentId));
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findByClientId(Long clientId) {
        List<Order> orders = orderRepository.findByClientId(clientId);
        return orderMapper.toResponseDTOList(orders);
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com o ID: " + id));
        return orderMapper.toResponseDTO(order);
    }

    @Transactional
    public OrderResponseDTO updateStatus(Long orderId, String statusStr) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com o ID: " + orderId));
        
        OrderStatus newStatus = OrderStatus.valueOf(statusStr.toUpperCase());
        
        // LÓGICA DE ESTORNO DE ESTOQUE
        // Se o novo status for CANCELADO e o pedido NÃO estava cancelado antes
        if (newStatus == OrderStatus.CANCELADO && order.getStatus() != OrderStatus.CANCELADO) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                int quantityToReturn = item.getWeightInGrams() != null ? item.getWeightInGrams() : item.getQuantity();
                
                product.setStockQuantity(product.getStockQuantity() + quantityToReturn);
                productRepository.save(product);
            }
        }
        // Opcional: Se quiser permitir "descancelar" (voltar a ser pendente), teria que tirar do estoque de novo.
        // Por segurança, geralmente não permitimos descancelar automaticamente sem verificar estoque, 
        // mas aqui vamos focar apenas no cancelamento.

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
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
