package br.com.acougue.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyRevenueDTO {
    private LocalDate date;
    private BigDecimal revenue;

    public DailyRevenueDTO(LocalDate date, BigDecimal revenue) {
        this.date = date;
        this.revenue = revenue;
    }

    // Getters
    public LocalDate getDate() { return date; }
    public BigDecimal getRevenue() { return revenue; }
}
