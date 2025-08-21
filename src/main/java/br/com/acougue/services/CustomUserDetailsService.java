package br.com.acougue.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.acougue.entities.Establishment;
import br.com.acougue.repository.EstablishmentRepository;
import br.com.acougue.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Primeiro tenta encontrar como User
        var userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            return userOpt.get(); // User já implementa UserDetails
        }
        
        // Se não encontrar, tenta como Establishment
        var establishmentOpt = establishmentRepository.findByUsername(username);
        if (establishmentOpt.isPresent()) {
            return createUserDetailsFromEstablishment(establishmentOpt.get());
        }
        
        throw new UsernameNotFoundException("Usuário não encontrado: " + username);
    }
    
    /**
     * Cria um UserDetails a partir de um Establishment
     */
    private UserDetails createUserDetailsFromEstablishment(Establishment establishment) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(establishment.getUsername())
                .password(establishment.getPassword())
                .authorities(establishment.getRole())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}