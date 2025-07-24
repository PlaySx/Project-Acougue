package br.com.acougue.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.acougue.entities.Order;
import br.com.acougue.enums.OrderStatus;
import br.com.acougue.services.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	@PostMapping
	public Order create(@RequestBody Order order) {
		return orderService.save(order);
	}
	
	@GetMapping
	public List<Order> findAll(){
		return orderService.findAll();
	}
	
	@GetMapping("/{id}")
	public Order findById(@PathVariable Long id) {
		return orderService.findById(id);
	}
	
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		orderService.delete(id);
	}
	
	@PatchMapping("/{id}/status")
	public Order updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
		return orderService.updateStatus(id, status);
	}
}
