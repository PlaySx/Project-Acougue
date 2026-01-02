package br.com.acougue.services;

import br.com.acougue.dto.DailyRevenueDTO;
import br.com.acougue.dto.DashboardDataDTO;
import br.com.acougue.dto.TopProductDTO;
import br.com.acougue.entities.Order;
import br.com.acougue.mapper.OrderMapper;
import br.com.acougue.repository.ClientRepository;
import br.com.acougue.repository.OrderRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode; // 1. Importa o enum RoundingMode
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final OrderMapper orderMapper;

    public DashboardService(OrderRepository orderRepository, ClientRepository clientRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
        this.orderMapper = orderMapper;
    }

    public DashboardDataDTO getDashboardData(Long establishmentId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = (startDate != null) ? startDate.atStartOfDay() : LocalDate.now().atStartOfDay();
        LocalDateTime end = (endDate != null) ? endDate.atTime(LocalTime.MAX) : LocalDate.now().atTime(LocalTime.MAX);

        DashboardDataDTO data = new DashboardDataDTO();

        Long totalOrders = orderRepository.countByEstablishmentIdAndDatahoraBetween(establishmentId, start, end);
        BigDecimal totalRevenue = orderRepository.sumTotalValueByEstablishmentIdAndDatahoraBetween(establishmentId, start, end);
        Long newClients = clientRepository.countByEstablishmentIdAndCreatedAtBetween(establishmentId, start, end);

        data.setTotalOrdersToday(totalOrders != null ? totalOrders : 0L);
        data.setTotalRevenueToday(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        data.setNewClientsToday(newClients != null ? newClients : 0L);

        if (data.getTotalOrdersToday() > 0 && data.getTotalRevenueToday().compareTo(BigDecimal.ZERO) > 0) {
            // 2. CORREÇÃO: Usa o enum RoundingMode.HALF_UP em vez do int
            data.setAverageTicketToday(data.getTotalRevenueToday().divide(BigDecimal.valueOf(data.getTotalOrdersToday()), 2, RoundingMode.HALF_UP));
        } else {
            data.setAverageTicketToday(BigDecimal.ZERO);
        }

        Map<String, Long> safeStatusCount = orderRepository.countByEstablishmentIdAndDatahoraBetweenGroupByStatus(establishmentId, start, end)
                .entrySet().stream()
                .filter(entry -> entry.getKey() != null)
                .collect(Collectors.toMap(entry -> entry.getKey().name(), Map.Entry::getValue));
        data.setOrderStatusCount(safeStatusCount);

        List<TopProductDTO> topProducts = orderRepository.findTopSellingProducts(establishmentId, start, end, PageRequest.of(0, 5));
        data.setTopSellingProducts(topProducts);

        List<DailyRevenueDTO> dailyRevenue = orderRepository.findDailyRevenue(establishmentId, start, end);
        data.setWeeklyRevenue(dailyRevenue);

        List<Order> recentOrders = orderRepository.findRecentOrdersByEstablishmentId(establishmentId, LocalDate.now().minusDays(30).atStartOfDay());
        data.setRecentOrders(orderMapper.toResponseDTOList(recentOrders.stream().limit(5).collect(Collectors.toList())));

        return data;
    }
}
