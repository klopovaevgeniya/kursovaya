package com.example.AutoDetail.controller;

import com.example.AutoDetail.dto.OrderDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order API", description = "Операции с заказами")
public class OrderController {

    @GetMapping
    @Operation(
            summary = "Получить все заказы",
            description = "Возвращает список всех заказов"
    )
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить заказ по ID",
            description = "Возвращает заказ по указанному идентификатору"
    )
    public ResponseEntity<OrderDto> getOrderById(
            @Parameter(description = "ID заказа", example = "1", required = true)
            @PathVariable Long id) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/client/{clientId}")
    @Operation(
            summary = "Получить заказы клиента",
            description = "Возвращает все заказы указанного клиента"
    )
    public ResponseEntity<List<OrderDto>> getOrdersByClientId(
            @Parameter(description = "ID клиента", example = "1", required = true)
            @PathVariable Long clientId) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/status/{statusId}")
    @Operation(
            summary = "Получить заказы по статусу",
            description = "Возвращает все заказы с указанным статусом"
    )
    public ResponseEntity<List<OrderDto>> getOrdersByStatus(
            @Parameter(description = "ID статуса", example = "1", required = true)
            @PathVariable Long statusId) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/date-range")
    @Operation(
            summary = "Получить заказы за период",
            description = "Возвращает заказы за указанный период времени"
    )
    public ResponseEntity<List<OrderDto>> getOrdersByDateRange(
            @Parameter(description = "Дата начала периода", example = "2023-12-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Дата окончания периода", example = "2023-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/amount-range")
    @Operation(
            summary = "Получить заказы по сумме",
            description = "Возвращает заказы в указанном диапазоне сумм"
    )
    public ResponseEntity<List<OrderDto>> getOrdersByAmountRange(
            @Parameter(description = "Минимальная сумма", example = "1000", required = true)
            @RequestParam Double minAmount,
            @Parameter(description = "Максимальная сумма", example = "5000", required = true)
            @RequestParam Double maxAmount) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/recent")
    @Operation(
            summary = "Получить последние заказы",
            description = "Возвращает последние N заказов"
    )
    public ResponseEntity<List<OrderDto>> getRecentOrders(
            @Parameter(description = "Количество заказов", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}/full-info")
    @Operation(
            summary = "Получить полную информацию о заказе",
            description = "Возвращает расширенную информацию о заказе с деталями клиента и статуса"
    )
    public ResponseEntity<OrderDto> getOrderFullInfo(
            @Parameter(description = "ID заказа", example = "1", required = true)
            @PathVariable Long id) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats/total-amount")
    @Operation(
            summary = "Получить общую сумму заказов",
            description = "Возвращает общую сумму всех заказов"
    )
    public ResponseEntity<Double> getTotalOrdersAmount() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(0.0);
    }

    @GetMapping("/stats/count-by-status")
    @Operation(
            summary = "Получить количество заказов по статусам",
            description = "Возвращает статистику по количеству заказов в каждом статусе"
    )
    public ResponseEntity<Object> getOrdersCountByStatus() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats/daily")
    @Operation(
            summary = "Получить ежедневную статистику",
            description = "Возвращает статистику заказов за сегодня"
    )
    public ResponseEntity<Object> getDailyStats() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }
}