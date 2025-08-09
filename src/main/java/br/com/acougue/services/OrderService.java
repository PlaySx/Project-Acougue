package br.com.acougue.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.acougue.dto.OrderRequestDTO;
import br.com.acougue.dto.OrderResponseDTO;
import br.com.acougue.entities.Order;
import br.com.acougue.enums.OrderStatus;
import br.com.acougue.globalException.OrderNaoEncontradoException;
import br.com.acougue.mapper.OrderMapper;
import br.com.acougue.repository.OrderRepository;

@Service
public class OrderService {
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderMapper orderMapper;
	
	@Transactional
	public OrderResponseDTO create(OrderRequestDTO orderRequestDTO) {
		Order order = orderMapper.toEntity(orderRequestDTO);
		order.setStatus(OrderStatus.PENDENTE); // Define status inicial
		Order savedOrder = orderRepository.save(order);
		return orderMapper.toResponseDTO(savedOrder);
	}
	
	public List<OrderResponseDTO> findAll(){
		List<Order> orders = orderRepository.findAll();
		return orderMapper.toResponseDTOList(orders);
	}
	
	public Optional<OrderResponseDTO> findById(Long id) {
		return orderRepository.findById(id)
				.map(orderMapper::toResponseDTO);
	}
	
	@Transactional
	public OrderResponseDTO update(Long id, OrderRequestDTO orderRequestDTO) {
		Order existingOrder = orderRepository.findById(id)
				.orElseThrow(() -> new OrderNaoEncontradoException(id));
		
		orderMapper.updateEntityFromDTO(existingOrder, orderRequestDTO);
		Order updatedOrder = orderRepository.save(existingOrder);
		return orderMapper.toResponseDTO(updatedOrder);
	}
	
	@Transactional
	public void delete(Long id) {
		if(!orderRepository.existsById(id)) {
			throw new OrderNaoEncontradoException(id);
		}
		orderRepository.deleteById(id);
	}
	
	@Transactional
	public OrderResponseDTO updateStatus(Long id, OrderStatus newStatus) {
		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new OrderNaoEncontradoException(id));
		
		if(order.getStatus() == OrderStatus.CANCELADO) {
			throw new IllegalStateException("Não é possível alterar um pedido cancelado.");
		}
		
		order.setStatus(newStatus);
		Order updatedOrder = orderRepository.save(order);
		return orderMapper.toResponseDTO(updatedOrder);
	}
}