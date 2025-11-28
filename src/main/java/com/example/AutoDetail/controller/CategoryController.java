package com.example.AutoDetail.controller;

import com.example.AutoDetail.dto.CategoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category API", description = "Операции с категориями товаров")
public class CategoryController {

    @GetMapping
    @Operation(
            summary = "Получить все категории",
            description = "Возвращает список всех категорий товаров"
    )
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить категорию по ID",
            description = "Возвращает категорию по указанному идентификатору"
    )
    public ResponseEntity<CategoryDto> getCategoryById(
            @Parameter(description = "ID категории", example = "1", required = true)
            @PathVariable Long id) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search/name/{name}")
    @Operation(
            summary = "Поиск категорий по названию",
            description = "Возвращает категории, содержащие указанное название"
    )
    public ResponseEntity<List<CategoryDto>> getCategoriesByName(
            @Parameter(description = "Название категории", example = "масло", required = true)
            @PathVariable String name) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}/items-count")
    @Operation(
            summary = "Получить количество товаров в категории",
            description = "Возвращает количество товаров в указанной категории"
    )
    public ResponseEntity<Integer> getItemsCountByCategory(
            @Parameter(description = "ID категории", example = "1", required = true)
            @PathVariable Long id) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(0);
    }

    @GetMapping("/with-items")
    @Operation(
            summary = "Получить категории с товарами",
            description = "Возвращает только категории, в которых есть товары"
    )
    public ResponseEntity<List<CategoryDto>> getCategoriesWithItems() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/without-items")
    @Operation(
            summary = "Получить пустые категории",
            description = "Возвращает только категории, в которых нет товаров"
    )
    public ResponseEntity<List<CategoryDto>> getCategoriesWithoutItems() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/stats/count")
    @Operation(
            summary = "Получить статистику по категориям",
            description = "Возвращает общее количество категорий и другую статистику"
    )
    public ResponseEntity<Object> getCategoriesStats() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/full-info")
    @Operation(
            summary = "Получить полную информацию о категории",
            description = "Возвращает расширенную информацию о категории"
    )
    public ResponseEntity<CategoryDto> getCategoryFullInfo(
            @Parameter(description = "ID категории", example = "1", required = true)
            @PathVariable Long id) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }
}