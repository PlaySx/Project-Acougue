package br.com.acougue.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.acougue.entities.Establishment;

public interface EstablishmentRepository extends JpaRepository<Establishment, Long> {

}
