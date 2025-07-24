package br.com.acougue.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.acougue.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
