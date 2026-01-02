package br.com.acougue.repository;

import br.com.acougue.entities.Establishment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstablishmentRepository extends JpaRepository<Establishment, Long> {
    // Novo método para verificar se um CNPJ já existe
    boolean existsByCnpj(String cnpj);
}
