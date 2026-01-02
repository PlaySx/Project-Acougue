package br.com.acougue.services;

import br.com.acougue.entities.Order;
import br.com.acougue.entities.OrderItem;
import br.com.acougue.repository.OrderRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final OrderRepository orderRepository;

    public ReportService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public ByteArrayInputStream exportOrdersToExcel(Long establishmentId) throws IOException {
        List<Order> orders = orderRepository.findByEstablishmentId(establishmentId);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Pedidos");

            // Estilo do Cabeçalho
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Cabeçalho
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Data", "Cliente", "Status", "Pagamento", "Valor Total", "Itens"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Dados
            int rowIdx = 1;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Order order : orders) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(order.getId());
                row.createCell(1).setCellValue(order.getDatahora().format(dateFormatter));
                row.createCell(2).setCellValue(order.getClient().getName());
                row.createCell(3).setCellValue(order.getStatus().name());
                row.createCell(4).setCellValue(order.getPaymentMethod());
                row.createCell(5).setCellValue(order.getTotalValue().doubleValue());

                // Formata os itens em uma única string para caber na célula
                String itemsSummary = order.getItems().stream()
                        .map(item -> {
                            String qtd = item.getWeightInGrams() != null 
                                ? String.format("%.3f kg", item.getWeightInGrams() / 1000.0) 
                                : item.getQuantity() + " un";
                            return item.getProduct().getName() + " (" + qtd + ")";
                        })
                        .collect(Collectors.joining(", "));
                
                row.createCell(6).setCellValue(itemsSummary);
            }

            // Ajusta largura das colunas
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
