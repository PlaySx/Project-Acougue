package br.com.acougue.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.acougue.repository.ClientRepository;
import br.com.acougue.repository.OrderRepository;
import br.com.acougue.repository.ProductsRepository;

@Service
public class DashboardService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Retorna estatísticas do estabelecimento
     */
    public Map<String, Object> getEstablishmentStatistics(Long establishmentId) {
        if (establishmentId == null) {
            throw new IllegalArgumentException("ID do estabelecimento não pode ser nulo");
        }

        Map<String, Object> stats = new HashMap<>();
        
        // Contadores básicos
        Long totalClients = clientRepository.countByEstablishmentId(establishmentId);
        Long totalProducts = productsRepository.countByEstablishmentId(establishmentId);
        Long totalOrders = orderRepository.countByEstablishmentId(establishmentId);
        
        // Pedidos recentes (último mês)
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        Long recentOrders = (long) orderRepository.findRecentOrdersByEstablishmentId(establishmentId, oneMonthAgo).size();
        
        stats.put("totalClients", totalClients);
        stats.put("totalProducts", totalProducts);
        stats.put("totalOrders", totalOrders);
        stats.put("recentOrders", recentOrders);
        stats.put("lastUpdated", LocalDateTime.now());
        
        return stats;
    }
}