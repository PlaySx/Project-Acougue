package br.com.acougue.services;

import br.com.acougue.dto.DashboardDataDTO;
import br.com.acougue.dto.TopProductDTO;
import br.com.acougue.enums.OrderStatus;
import br.com.acougue.repository.ClientRepository;
import br.com.acougue.repository.OrderRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;

    public DashboardService(OrderRepository orderRepository, ClientRepository clientRepository) {
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
    }

    public DashboardDataDTO getDashboardData(Long establishmentId) {
        DashboardDataDTO data = new DashboardDataDTO();
        LocalDate today = LocalDate.now();

        // KPIs
        Long totalOrdersToday = orderRepository.countByEstablishmentIdAndDatahoraBetween(establishmentId, today.atStartOfDay(), today.atTime(LocalTime.MAX));
        BigDecimal totalRevenueToday = orderRepository.sumTotalValueByEstablishmentIdAndDatahoraBetween(establishmentId, today.atStartOfDay(), today.atTime(LocalTime.MAX));
        Long newClientsToday = clientRepository.countByEstablishmentIdAndCreatedAtBetween(establishmentId, today.atStartOfDay(), today.atTime(LocalTime.MAX));

        data.setTotalOrdersToday(totalOrdersToday != null ? totalOrdersToday : 0L);
        data.setTotalRevenueToday(totalRevenueToday != null ? totalRevenueToday : BigDecimal.ZERO);
        data.setNewClientsToday(newClientsToday != null ? newClientsToday : 0L);

        if (data.getTotalOrdersToday() > 0 && data.getTotalRevenueToday().compareTo(BigDecimal.ZERO) > 0) {
            data.setAverageTicketToday(data.getTotalRevenueToday().divide(BigDecimal.valueOf(data.getTotalOrdersToday()), 2, BigDecimal.ROUND_HALF_UP));
        } else {
            data.setAverageTicketToday(BigDecimal.ZERO);
        }

        // Gráfico de Status
        Map<OrderStatus, Long> rawStatusCount = orderRepository.countByEstablishmentIdGroupByStatus(establishmentId);
        Map<String, Long> safeStatusCount = rawStatusCount.entrySet().stream()
                .filter(entry -> entry.getKey() != null)
                .collect(Collectors.toMap(entry -> entry.getKey().name(), Map.Entry::getValue));
        data.setOrderStatusCount(safeStatusCount);

        // Gráfico de Top Produtos
        List<TopProductDTO> topProducts = orderRepository.findTopSellingProducts(establishmentId, PageRequest.of(0, 5));
        data.setTopSellingProducts(topProducts);

        return data;
    }
}
