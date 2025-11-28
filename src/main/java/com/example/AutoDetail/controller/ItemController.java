package com.example.AutoDetail.controller;

import com.example.AutoDetail.dto.ItemDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@Tag(name = "Item API", description = "Операции с товарами")
public class ItemController {

    @GetMapping
    @Operation(
            summary = "Получить все товары",
            description = "Возвращает список всех товаров с полной информацией"
    )
    public ResponseEntity<List<ItemDto>> getAllItems() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить товар по ID",
            description = "Возвращает товар по указанному идентификатору"
    )
    public ResponseEntity<ItemDto> getItemById(
            @Parameter(description = "ID товара", example = "1", required = true)
            @PathVariable Long id) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search/articul/{articul}")
    @Operation(
            summary = "Найти товар по артикулу",
            description = "Возвращает товар по артикулу"
    )
    public ResponseEntity<ItemDto> getItemByArticul(
            @Parameter(description = "Артикул товара", example = "OIL-5W30-001", required = true)
            @PathVariable String articul) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search/name/{name}")
    @Operation(
            summary = "Поиск товаров по названию",
            description = "Возвращает товары, содержащие указанное название"
    )
    public ResponseEntity<List<ItemDto>> getItemsByName(
            @Parameter(description = "Название товара", example = "масло", required = true)
            @PathVariable String name) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/category/{categoryId}")
    @Operation(
            summary = "Получить товары по категории",
            description = "Возвращает все товары указанной категории"
    )
    public ResponseEntity<List<ItemDto>> getItemsByCategory(
            @Parameter(description = "ID категории", example = "1", required = true)
            @PathVariable Long categoryId) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/supplier/{supplierId}")
    @Operation(
            summary = "Получить товары по поставщику",
            description = "Возвращает все товары указанного поставщика"
    )
    public ResponseEntity<List<ItemDto>> getItemsBySupplier(
            @Parameter(description = "ID поставщика", example = "1", required = true)
            @PathVariable Long supplierId) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/in-stock")
    @Operation(
            summary = "Получить товары в наличии",
            description = "Возвращает только товары, которые есть в наличии"
    )
    public ResponseEntity<List<ItemDto>> getItemsInStock() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/out-of-stock")
    @Operation(
            summary = "Получить товары не в наличии",
            description = "Возвращает только товары, которых нет в наличии"
    )
    public ResponseEntity<List<ItemDto>> getItemsOutOfStock() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/price-range")
    @Operation(
            summary = "Получить товары в диапазоне цен",
            description = "Возвращает товары в указанном диапазоне цен"
    )
    public ResponseEntity<List<ItemDto>> getItemsByPriceRange(
            @Parameter(description = "Минимальная цена", example = "1000", required = true)
            @RequestParam Double minPrice,
            @Parameter(description = "Максимальная цена", example = "3000", required = true)
            @RequestParam Double maxPrice) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/low-stock")
    @Operation(
            summary = "Получить товары с низким запасом",
            description = "Возвращает товары, количество которых меньше 10"
    )
    public ResponseEntity<List<ItemDto>> getLowStockItems() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/stats/inventory")
    @Operation(
            summary = "Получить статистику по товарам",
            description = "Возвращает общую статистику по товарам на складе"
    )
    public ResponseEntity<Object> getInventoryStats() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }
}