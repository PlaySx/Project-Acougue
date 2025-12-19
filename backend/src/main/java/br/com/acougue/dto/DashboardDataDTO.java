package br.com.acougue.dto;

import br.com.acougue.enums.OrderStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardDataDTO {

    private Long totalOrdersToday;
    private BigDecimal totalRevenueToday;
    private Long newClientsToday;
    private BigDecimal averageTicketToday;
    private Map<String, Long> orderStatusCount;
    
    // Novo campo para o gráfico de produtos
    private List<TopProductDTO> topSellingProducts;

    public DashboardDataDTO() {}

    // Getters e Setters
    public Long getTotalOrdersToday() { return totalOrdersToday; }
    public void setTotalOrdersToday(Long totalOrdersToday) { this.totalOrdersToday = totalOrdersToday; }

    public BigDecimal getTotalRevenueToday() { return totalRevenueToday; }
    public void setTotalRevenueToday(BigDecimal totalRevenueToday) { this.totalRevenueToday = totalRevenueToday; }

    public Long getNewClientsToday() { return newClientsToday; }
    public void setNewClientsToday(Long newClientsToday) { this.newClientsToday = newClientsToday; }

    public BigDecimal getAverageTicketToday() { return averageTicketToday; }
    public void setAverageTicketToday(BigDecimal averageTicketToday) { this.averageTicketToday = averageTicketToday; }

    public Map<String, Long> getOrderStatusCount() { return orderStatusCount; }
    public void setOrderStatusCount(Map<String, Long> orderStatusCount) { this.orderStatusCount = orderStatusCount; }

    public List<TopProductDTO> getTopSellingProducts() { return topSellingProducts; }
    public void setTopSellingProducts(List<TopProductDTO> topSellingProducts) { this.topSellingProducts = topSellingProducts; }
}
