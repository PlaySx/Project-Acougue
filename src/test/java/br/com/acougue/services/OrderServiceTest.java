package br.com.acougue.services;

import br.com.acougue.dto.OrderRequestDTO;
import br.com.acougue.dto.OrderResponseDTO;
import br.com.acougue.entities.Order;
import br.com.acougue.enums.OrderStatus;
import br.com.acougue.exceptions.ResourceNotFoundException;
import br.com.acougue.mapper.OrderMapper;
import br.com.acougue.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private OrderRequestDTO orderRequestDTO;
    private OrderResponseDTO orderResponseDTO;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDENTE);
        order.setDatahora(LocalDateTime.now());

        orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setClientId(1L);
        orderRequestDTO.setEstablishmentId(1L);
        orderRequestDTO.setProductIds(List.of(1L, 2L));

        orderResponseDTO = new OrderResponseDTO();
        orderResponseDTO.setId(1L);
        orderResponseDTO.setStatus(OrderStatus.PENDENTE);
    }

    @Test
    void create_shouldSaveAndReturnOrderDTO() {
        // Arrange
        when(orderMapper.toEntity(any(OrderRequestDTO.class))).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toResponseDTO(any(Order.class))).thenReturn(orderResponseDTO);

        // Act
        OrderResponseDTO result = orderService.create(orderRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.PENDENTE, result.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void findById_shouldReturnOrderDTO_whenOrderExists() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toResponseDTO(order)).thenReturn(orderResponseDTO);

        // Act
        OrderResponseDTO result = orderService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void findById_shouldThrowResourceNotFoundException_whenOrderDoesNotExist() {
        // Arrange
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.findById(99L);
        });
    }

    @Test
    void updateStatus_shouldUpdateAndReturnDTO_whenOrderExists() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toResponseDTO(order)).thenReturn(orderResponseDTO);

        // Act
        orderResponseDTO.setStatus(OrderStatus.ENTREGUE); // Simula a resposta do mapper
        OrderResponseDTO result = orderService.updateStatus(1L, OrderStatus.ENTREGUE);

        // Assert
        assertEquals(OrderStatus.ENTREGUE, order.getStatus());
        assertNotNull(result);
        assertEquals(OrderStatus.ENTREGUE, result.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void update_shouldUpdateAndReturnDTO_whenOrderExists() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toResponseDTO(order)).thenReturn(orderResponseDTO);

        // Act
        orderResponseDTO.setStatus(OrderStatus.A_CAMINHO); // Simula a resposta do mapper
        OrderResponseDTO result = orderService.update(1L, orderRequestDTO);

        // Assert
        verify(orderMapper, times(1)).updateEntityFromDTO(order, orderRequestDTO);
        verify(orderRepository, times(1)).save(order);
        assertNotNull(result);
        assertEquals(OrderStatus.A_CAMINHO, result.getStatus());
    }

    @Test
    void update_shouldThrowResourceNotFoundException_whenOrderDoesNotExist() {
        // Arrange
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.update(99L, new OrderRequestDTO());
        });

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void delete_shouldCallDelete_whenOrderExists() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doNothing().when(orderRepository).delete(order);

        // Act
        orderService.delete(1L);

        // Assert
        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    void delete_shouldThrowResourceNotFoundException_whenOrderDoesNotExist() {
        // Arrange
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.delete(99L);
        });

        verify(orderRepository, never()).delete(any(Order.class));
    }

    @Test
    void findAll_shouldReturnListOfAllOrders() {
        // Arrange
        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(orderMapper.toResponseDTOList(anyList())).thenReturn(List.of(orderResponseDTO));

        // Act
        List<OrderResponseDTO> result = orderService.findAll();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void findByClientId_shouldReturnOrdersForThatClient() {
        // Arrange
        when(orderRepository.findByClientId(1L)).thenReturn(List.of(order));
        when(orderMapper.toResponseDTOList(anyList())).thenReturn(List.of(orderResponseDTO));

        // Act
        List<OrderResponseDTO> result = orderService.findByClientId(1L);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(orderRepository, times(1)).findByClientId(1L);
    }
}