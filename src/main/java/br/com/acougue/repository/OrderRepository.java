package br.com.acougue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.acougue.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
