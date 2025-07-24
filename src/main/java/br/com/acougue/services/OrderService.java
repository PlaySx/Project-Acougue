package br.com.acougue.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.acougue.entities.Order;
import br.com.acougue.enums.OrderStatus;
import br.com.acougue.globalException.OrderNaoEncontradoException;
import br.com.acougue.repository.OrderRepository;

@Service
public class OrderService {
	
	@Autowired
	private OrderRepository orderRepository;
	
	public Order save(Order order) {
		return orderRepository.save(order);
	}
	
	public List<Order> findAll(){
		return orderRepository.findAll();
	}
	
	public Order findById(Long id) {
		return orderRepository.findById(id)
				.orElseThrow(() -> new OrderNaoEncontradoException(id));
	}
	
	public void delete(Long id) {
		if(!orderRepository.existsById(id)) {
			throw new OrderNaoEncontradoException(id);
		}
		orderRepository.deleteById(id);
	}
	
	@Transactional
	public Order updateStatus(Long id, OrderStatus newStatus) {
		Order pedido = findById(id);
		if(pedido.getStatus() == OrderStatus.CANCELADO) {
			throw new IllegalStateException("Não é possível alterar um pedido cancelado.");
		}
		pedido.setStatus(newStatus);
		return orderRepository.save(pedido);
	}
	
}
