package com.example.AutoDetail.service;

import com.example.AutoDetail.entity.*;
import com.example.AutoDetail.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    public ReportService(OrderRepository orderRepository,
                         ItemRepository itemRepository,
                         UserRepository userRepository,
                         ClientRepository clientRepository) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
    }

    // === ОТЧЕТ ПО ПРОДАЖАМ (ЗАКАЗАМ) ===
    public byte[] generateSalesReport(LocalDate startDate, LocalDate endDate) throws IOException {
        List<Order> orders = orderRepository.findOrdersByDateRange(
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        );

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Отчет по продажам");

            // Стили
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle moneyStyle = createMoneyStyle(workbook);

            // Заголовок отчета
            createReportHeader(sheet, "ОТЧЕТ ПО ПРОДАЖАМ", startDate, endDate);

            // Заголовки таблицы
            Row headerRow = sheet.createRow(3);
            String[] headers = {"№", "ID заказа", "Дата", "ID менеджера", "ID клиента", "Статус", "Сумма"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Данные заказов
            int rowNum = 4;
            double totalRevenue = 0.0;
            int totalOrders = orders.size();

            for (int i = 0; i < orders.size(); i++) {
                Order order = orders.get(i);
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(order.getId());
                row.createCell(2).setCellValue(order.getCreatedAt() != null ?
                        order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : "Не указана");
                row.createCell(3).setCellValue(order.getUserId() != null ? order.getUserId().toString() : "Не назначен");
                row.createCell(4).setCellValue(order.getClientId() != null ? order.getClientId().toString() : "Не указан");
                row.createCell(5).setCellValue(order.getStatusId() != null ? "Статус " + order.getStatusId() : "Не определен");

                Cell amountCell = row.createCell(6);
                double amount = order.getTotalAmount() != null ? order.getTotalAmount() : 0.0;
                amountCell.setCellValue(amount);
                amountCell.setCellStyle(moneyStyle);

                totalRevenue += amount;
            }

            // Итоги
            Row summaryRow = sheet.createRow(rowNum + 1);
            summaryRow.createCell(0).setCellValue("ИТОГО:");
            summaryRow.createCell(5).setCellValue("Всего заказов: " + totalOrders);

            Cell totalCell = summaryRow.createCell(6);
            totalCell.setCellValue(totalRevenue);
            totalCell.setCellStyle(moneyStyle);

            // Авто-размер колонок
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    // === ОТЧЕТ ПО ПОСТУПЛЕНИЯМ ТОВАРОВ ===
    public byte[] generateInventoryReport() throws IOException {
        List<Item> items = itemRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Отчет по товарам");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle moneyStyle = createMoneyStyle(workbook);

            createReportHeader(sheet, "ОТЧЕТ ПО ТОВАРАМ НА СКЛАДЕ", null, null);

            Row headerRow = sheet.createRow(3);
            String[] headers = {"№", "Артикул", "Название", "Категория", "Поставщик", "Цена", "Кол-во", "Общая стоимость"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 4;
            double totalValue = 0.0;
            int totalQuantity = 0;

            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(item.getArctical());
                row.createCell(2).setCellValue(item.getName());
                row.createCell(3).setCellValue(item.getCategory() != null ? item.getCategory().getName() : "Не указана");
                row.createCell(4).setCellValue(item.getSupplier() != null ? item.getSupplier().getName() : "Не указан");

                Cell priceCell = row.createCell(5);
                double price = item.getPrice() != null ? item.getPrice() : 0.0;
                priceCell.setCellValue(price);
                priceCell.setCellStyle(moneyStyle);

                int quantity = item.getQuantity() != null ? item.getQuantity() : 0;
                row.createCell(6).setCellValue(quantity);

                double itemValue = price * quantity;

                Cell valueCell = row.createCell(7);
                valueCell.setCellValue(itemValue);
                valueCell.setCellStyle(moneyStyle);

                totalValue += itemValue;
                totalQuantity += quantity;
            }

            // Итоги
            Row summaryRow = sheet.createRow(rowNum + 1);
            summaryRow.createCell(0).setCellValue("ИТОГО:");
            summaryRow.createCell(6).setCellValue(totalQuantity);

            Cell totalValueCell = summaryRow.createCell(7);
            totalValueCell.setCellValue(totalValue);
            totalValueCell.setCellStyle(moneyStyle);

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    // === ОТЧЕТ ПО ЭФФЕКТИВНОСТИ МЕНЕДЖЕРОВ ===
    public byte[] generateManagersReport(LocalDate startDate, LocalDate endDate) throws IOException {
        List<User> managers = userRepository.findByRole(Role.ROLE_MANAGER);
        List<Order> orders = orderRepository.findOrdersByDateRange(
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        );

        // Группируем заказы по менеджерам (userId)
        Map<Long, List<Order>> ordersByManager = orders.stream()
                .filter(order -> order.getUserId() != null)
                .collect(Collectors.groupingBy(Order::getUserId));

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Отчет по менеджерам");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle moneyStyle = createMoneyStyle(workbook);

            createReportHeader(sheet, "ОТЧЕТ ПО ЭФФЕКТИВНОСТИ МЕНЕДЖЕРОВ", startDate, endDate);

            Row headerRow = sheet.createRow(3);
            String[] headers = {"№", "Менеджер", "ID", "Кол-во заказов", "Общая сумма", "Средний чек", "Доля от общих продаж"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 4;
            double totalRevenue = orders.stream()
                    .mapToDouble(order -> order.getTotalAmount() != null ? order.getTotalAmount() : 0.0)
                    .sum();

            for (int i = 0; i < managers.size(); i++) {
                User manager = managers.get(i);
                List<Order> managerOrders = ordersByManager.getOrDefault(manager.getId(), List.of());

                Row row = sheet.createRow(rowNum++);

                double managerRevenue = managerOrders.stream()
                        .mapToDouble(order -> order.getTotalAmount() != null ? order.getTotalAmount() : 0.0)
                        .sum();

                double averageOrder = managerOrders.size() > 0 ? managerRevenue / managerOrders.size() : 0.0;
                double share = totalRevenue > 0 ? (managerRevenue / totalRevenue) * 100 : 0.0;

                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(manager.getName());
                row.createCell(2).setCellValue(manager.getId());
                row.createCell(3).setCellValue(managerOrders.size());

                Cell revenueCell = row.createCell(4);
                revenueCell.setCellValue(managerRevenue);
                revenueCell.setCellStyle(moneyStyle);

                Cell avgCell = row.createCell(5);
                avgCell.setCellValue(averageOrder);
                avgCell.setCellStyle(moneyStyle);

                row.createCell(6).setCellValue(String.format("%.2f%%", share));
            }

            // Общая статистика
            Row summaryRow = sheet.createRow(rowNum + 2);
            summaryRow.createCell(0).setCellValue("ОБЩАЯ СТАТИСТИКА:");
            summaryRow.createCell(1).setCellValue("Всего менеджеров: " + managers.size());
            summaryRow.createCell(2).setCellValue("Всего заказов: " + orders.size());

            Cell totalCell = summaryRow.createCell(4);
            totalCell.setCellValue(totalRevenue);
            totalCell.setCellStyle(moneyStyle);

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    // === ОТЧЕТ ПО КЛИЕНТАМ И ИХ ЗАКАЗАМ ===
    public byte[] generateClientsReport(LocalDate startDate, LocalDate endDate) throws IOException {
        List<Client> clients = clientRepository.findAll();
        List<Order> orders = orderRepository.findOrdersByDateRange(
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        );

        // Группируем заказы по клиентам
        Map<Long, List<Order>> ordersByClient = orders.stream()
                .filter(order -> order.getClientId() != null)
                .collect(Collectors.groupingBy(Order::getClientId));

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Отчет по клиентам");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle moneyStyle = createMoneyStyle(workbook);

            createReportHeader(sheet, "ОТЧЕТ ПО КЛИЕНТАМ И ИХ АКТИВНОСТИ", startDate, endDate);

            Row headerRow = sheet.createRow(3);
            String[] headers = {"№", "Клиент", "Телефон", "Email", "Кол-во заказов", "Общая сумма", "Средний чек"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 4;
            int activeClients = 0;

            for (int i = 0; i < clients.size(); i++) {
                Client client = clients.get(i);
                List<Order> clientOrders = ordersByClient.getOrDefault(client.getId(), List.of());

                if (clientOrders.isEmpty()) continue;

                activeClients++;
                Row row = sheet.createRow(rowNum++);

                double clientRevenue = clientOrders.stream()
                        .mapToDouble(order -> order.getTotalAmount() != null ? order.getTotalAmount() : 0.0)
                        .sum();

                double averageOrder = clientOrders.size() > 0 ? clientRevenue / clientOrders.size() : 0.0;

                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(client.getFullName());
                row.createCell(2).setCellValue(client.getPhone() != null ? client.getPhone() : "Не указан");
                row.createCell(3).setCellValue(client.getEmail() != null ? client.getEmail() : "Не указан");
                row.createCell(4).setCellValue(clientOrders.size());

                Cell revenueCell = row.createCell(5);
                revenueCell.setCellValue(clientRevenue);
                revenueCell.setCellStyle(moneyStyle);

                Cell avgCell = row.createCell(6);
                avgCell.setCellValue(averageOrder);
                avgCell.setCellStyle(moneyStyle);
            }

            // Общая статистика
            Row summaryRow = sheet.createRow(rowNum + 2);
            summaryRow.createCell(0).setCellValue("ОБЩАЯ СТАТИСТИКА:");
            summaryRow.createCell(1).setCellValue("Всего клиентов: " + clients.size());
            summaryRow.createCell(2).setCellValue("Активных клиентов: " + activeClients);
            summaryRow.createCell(3).setCellValue("Всего заказов: " + orders.size());

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    // === ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===

    private void createReportHeader(Sheet sheet, String title, LocalDate startDate, LocalDate endDate) {
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(title);

        Row dateRow = sheet.createRow(1);
        String dateRange = "Период: " + (startDate != null && endDate != null ?
                startDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " +
                        endDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "За все время");
        dateRow.createCell(0).setCellValue(dateRange);

        Row generatedRow = sheet.createRow(2);
        generatedRow.createCell(0).setCellValue("Сгенерировано: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createMoneyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));
        return style;
    }
}