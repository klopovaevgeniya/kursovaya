package com.example.AutoDetail.controller;

import com.example.AutoDetail.dto.OrderItemDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
@Tag(name = "Order Item API", description = "Операции с позициями заказов")
public class OrderItemController {

    @GetMapping
    @Operation(
            summary = "Получить все позиции заказов",
            description = "Возвращает список всех позиций во всех заказах"
    )
    public ResponseEntity<List<OrderItemDto>> getAllOrderItems() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить позицию заказа по ID",
            description = "Возвращает позицию заказа по указанному идентификатору"
    )
    public ResponseEntity<OrderItemDto> getOrderItemById(
            @Parameter(description = "ID позиции заказа", example = "1", required = true)
            @PathVariable Long id) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/order/{orderId}")
    @Operation(
            summary = "Получить позиции заказа",
            description = "Возвращает все позиции указанного заказа"
    )
    public ResponseEntity<List<OrderItemDto>> getOrderItemsByOrderId(
            @Parameter(description = "ID заказа", example = "1", required = true)
            @PathVariable Long orderId) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/item/{itemId}")
    @Operation(
            summary = "Получить позиции по товару",
            description = "Возвращает все позиции заказов с указанным товаром"
    )
    public ResponseEntity<List<OrderItemDto>> getOrderItemsByItemId(
            @Parameter(description = "ID товара", example = "1", required = true)
            @PathVariable Long itemId) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/order/{orderId}/total")
    @Operation(
            summary = "Получить общую сумму заказа",
            description = "Возвращает общую сумму всех позиций указанного заказа"
    )
    public ResponseEntity<Double> getOrderTotalAmount(
            @Parameter(description = "ID заказа", example = "1", required = true)
            @PathVariable Long orderId) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(0.0);
    }

    @GetMapping("/order/{orderId}/items-count")
    @Operation(
            summary = "Получить количество позиций в заказе",
            description = "Возвращает количество различных товаров в заказе"
    )
    public ResponseEntity<Integer> getOrderItemsCount(
            @Parameter(description = "ID заказа", example = "1", required = true)
            @PathVariable Long orderId) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(0);
    }

    @GetMapping("/order/{orderId}/full-info")
    @Operation(
            summary = "Получить полную информацию о позициях заказа",
            description = "Возвращает расширенную информацию о всех позициях заказа"
    )
    public ResponseEntity<List<OrderItemDto>> getOrderItemsFullInfo(
            @Parameter(description = "ID заказа", example = "1", required = true)
            @PathVariable Long orderId) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/popular-items")
    @Operation(
            summary = "Получить популярные товары",
            description = "Возвращает самые популярные товары по количеству заказов"
    )
    public ResponseEntity<List<Object>> getPopularItems(
            @Parameter(description = "Количество товаров", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/stats/top-items")
    @Operation(
            summary = "Получить статистику по товарам",
            description = "Возвращает статистику по самым продаваемым товарам"
    )
    public ResponseEntity<Object> getTopItemsStats() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/full-info")
    @Operation(
            summary = "Получить полную информацию о позиции",
            description = "Возвращает расширенную информацию о позиции заказа с деталями товара и заказа"
    )
    public ResponseEntity<OrderItemDto> getOrderItemFullInfo(
            @Parameter(description = "ID позиции заказа", example = "1", required = true)
            @PathVariable Long id) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }
}