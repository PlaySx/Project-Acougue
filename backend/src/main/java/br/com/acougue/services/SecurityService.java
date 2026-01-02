package br.com.acougue.services;

import br.com.acougue.entities.Client;
import br.com.acougue.entities.Order;
import br.com.acougue.entities.Product;
import br.com.acougue.entities.User;
import br.com.acougue.repository.ClientRepository;
import br.com.acougue.repository.OrderRepository;
import br.com.acougue.repository.ProductRepository;
import br.com.acougue.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service("securityService")
public class SecurityService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public SecurityService(UserRepository userRepository, ClientRepository clientRepository, ProductRepository productRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) return null;
        return userRepository.findByEmail(authentication.getName()).orElse(null);
    }

    public boolean hasAccessToEstablishment(Long establishmentId) {
        User user = getAuthenticatedUser();
        if (user == null || user.getEstablishment() == null) return false;
        return user.getEstablishment().getId().equals(establishmentId);
    }

    @Transactional(readOnly = true)
    public boolean canAccessClient(Long clientId) {
        User user = getAuthenticatedUser();
        if (user == null || user.getEstablishment() == null) return false;
        
        Optional<Client> client = clientRepository.findById(clientId);
        return client.isPresent() && client.get().getEstablishment().getId().equals(user.getEstablishment().getId());
    }

    @Transactional(readOnly = true)
    public boolean canAccessProduct(Long productId) {
        User user = getAuthenticatedUser();
        if (user == null || user.getEstablishment() == null) return false;
        
        Optional<Product> product = productRepository.findById(productId);
        return product.isPresent() && product.get().getEstablishment().getId().equals(user.getEstablishment().getId());
    }

    @Transactional(readOnly = true)
    public boolean canAccessOrder(Long orderId) {
        User user = getAuthenticatedUser();
        if (user == null || user.getEstablishment() == null) return false;
        
        Optional<Order> order = orderRepository.findById(orderId);
        return order.isPresent() && order.get().getEstablishment().getId().equals(user.getEstablishment().getId());
    }
}
