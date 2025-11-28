package com.example.AutoDetail.controller;

import com.example.AutoDetail.dto.OrderStatusDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-statuses")
@Tag(name = "Order Status API", description = "Операции со статусами заказов")
public class OrderStatusController {

    @GetMapping
    @Operation(
            summary = "Получить все статусы заказов",
            description = "Возвращает список всех возможных статусов заказов"
    )
    public ResponseEntity<List<OrderStatusDto>> getAllOrderStatuses() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить статус заказа по ID",
            description = "Возвращает статус заказа по указанному идентификатору"
    )
    public ResponseEntity<OrderStatusDto> getOrderStatusById(
            @Parameter(description = "ID статуса заказа", example = "1", required = true)
            @PathVariable Long id) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search/status/{status}")
    @Operation(
            summary = "Поиск статуса по названию",
            description = "Возвращает статусы, содержащие указанное название"
    )
    public ResponseEntity<List<OrderStatusDto>> getOrderStatusesByStatus(
            @Parameter(description = "Название статуса", example = "обработ", required = true)
            @PathVariable String status) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}/orders-count")
    @Operation(
            summary = "Получить количество заказов по статусу",
            description = "Возвращает количество заказов с указанным статусом"
    )
    public ResponseEntity<Integer> getOrdersCountByStatus(
            @Parameter(description = "ID статуса", example = "1", required = true)
            @PathVariable Long id) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(0);
    }

    @GetMapping("/active")
    @Operation(
            summary = "Получить активные статусы",
            description = "Возвращает статусы, которые используются в текущих заказах"
    )
    public ResponseEntity<List<OrderStatusDto>> getActiveOrderStatuses() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/with-orders")
    @Operation(
            summary = "Получить статусы с заказами",
            description = "Возвращает только статусы, которые используются в заказах"
    )
    public ResponseEntity<List<OrderStatusDto>> getOrderStatusesWithOrders() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/without-orders")
    @Operation(
            summary = "Получить статусы без заказов",
            description = "Возвращает только статусы, которые не используются в заказах"
    )
    public ResponseEntity<List<OrderStatusDto>> getOrderStatusesWithoutOrders() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/stats/count")
    @Operation(
            summary = "Получить статистику по статусам",
            description = "Возвращает общее количество статусов и другую статистику"
    )
    public ResponseEntity<Object> getOrderStatusesStats() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/full-info")
    @Operation(
            summary = "Получить полную информацию о статусе",
            description = "Возвращает расширенную информацию о статусе заказа"
    )
    public ResponseEntity<OrderStatusDto> getOrderStatusFullInfo(
            @Parameter(description = "ID статуса заказа", example = "1", required = true)
            @PathVariable Long id) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }
}