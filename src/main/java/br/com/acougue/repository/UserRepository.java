package br.com.acougue.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.acougue.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Busca usuário por username. Ideal para login.
    Optional<User> findByUsername(String username);

    // Verifica se existe usuário com username
    boolean existsByUsername(String username);

    // Conta o total de todos os usuários
    @Query("SELECT COUNT(u) FROM User u")
    Long countAllUsers();

    // Conta usuários por uma função específica, como 'ROLE_EMPLOYEE' ou 'ROLE_OWNER'
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    Long countByRole(@Param("role") User.Role role);
    
    // Conta número de funcionários de um estabelecimento
    @Query("SELECT COUNT(u) FROM User u WHERE u.establishment.id = :establishmentId AND u.role = 'ROLE_EMPLOYEE'")
    Long countEmployeesByEstablishmentId(@Param("establishmentId") Long establishmentId);
    
    // Busca proprietários (ROLE_OWNER) de um estabelecimento
    @Query("SELECT u FROM User u WHERE u.establishment.id = :establishmentId AND u.role = 'ROLE_OWNER'")
    List<User> findOwnersByEstablishmentId(@Param("establishmentId") Long establishmentId);

    // Busca usuários por estabelecimento
    List<User> findByEstablishmentId(Long establishmentId);
    
    // Busca usuários por estabelecimento e role
    List<User> findByEstablishmentIdAndRole(Long establishmentId, User.Role role);
    
    // Busca usuários por role
    List<User> findByRole(User.Role role);
}