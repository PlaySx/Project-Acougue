package br.com.acougue.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.acougue.entities.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {

}
