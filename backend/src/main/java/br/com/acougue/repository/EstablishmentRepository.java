package br.com.acougue.repository; // seu pacote atual

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // import necessário para o Optional

import br.com.acougue.entities.Establishment;

@Repository
public interface EstablishmentRepository extends JpaRepository<Establishment, Long> {
	
    Optional<Establishment> findByUsername(String username);

    boolean existsByUsername(String username);

    List<Establishment> findByUsers_Id(Long userId);
}