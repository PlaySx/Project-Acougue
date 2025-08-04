package br.com.acougue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.acougue.entities.Establishment;

@Repository
public interface EstablishmentRepository extends JpaRepository<Establishment, Long> {

}
