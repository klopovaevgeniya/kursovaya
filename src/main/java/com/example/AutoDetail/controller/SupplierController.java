package com.example.AutoDetail.controller;

import com.example.AutoDetail.dto.SupplierDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@Tag(name = "Supplier API", description = "Операции с поставщиками")
public class SupplierController {

    @GetMapping
    @Operation(
            summary = "Получить всех поставщиков",
            description = "Возвращает список всех поставщиков"
    )
    public ResponseEntity<List<SupplierDto>> getAllSuppliers() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить поставщика по ID",
            description = "Возвращает поставщика по указанному идентификатору"
    )
    public ResponseEntity<SupplierDto> getSupplierById(
            @Parameter(description = "ID поставщика", example = "1", required = true)
            @PathVariable Long id) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search/name/{name}")
    @Operation(
            summary = "Поиск поставщиков по названию",
            description = "Возвращает поставщиков, содержащих указанное название"
    )
    public ResponseEntity<List<SupplierDto>> getSuppliersByName(
            @Parameter(description = "Название компании", example = "AutoParts", required = true)
            @PathVariable String name) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/search/phone/{phone}")
    @Operation(
            summary = "Найти поставщика по телефону",
            description = "Возвращает поставщика по номеру телефона"
    )
    public ResponseEntity<SupplierDto> getSupplierByPhone(
            @Parameter(description = "Номер телефона", example = "+7-800-123-45-67", required = true)
            @PathVariable String phone) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search/email/{email}")
    @Operation(
            summary = "Найти поставщика по email",
            description = "Возвращает поставщика по email адресу"
    )
    public ResponseEntity<SupplierDto> getSupplierByEmail(
            @Parameter(description = "Email адрес", example = "info@autoparts.ru", required = true)
            @PathVariable String email) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/items-count")
    @Operation(
            summary = "Получить количество товаров поставщика",
            description = "Возвращает количество товаров от указанного поставщика"
    )
    public ResponseEntity<Integer> getItemsCountBySupplier(
            @Parameter(description = "ID поставщика", example = "1", required = true)
            @PathVariable Long id) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(0);
    }

    @GetMapping("/with-items")
    @Operation(
            summary = "Получить поставщиков с товарами",
            description = "Возвращает только поставщиков, у которых есть товары"
    )
    public ResponseEntity<List<SupplierDto>> getSuppliersWithItems() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/without-items")
    @Operation(
            summary = "Получить поставщиков без товаров",
            description = "Возвращает только поставщиков, у которых нет товаров"
    )
    public ResponseEntity<List<SupplierDto>> getSuppliersWithoutItems() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}/full-info")
    @Operation(
            summary = "Получить полную информацию о поставщике",
            description = "Возвращает расширенную информацию о поставщике"
    )
    public ResponseEntity<SupplierDto> getSupplierFullInfo(
            @Parameter(description = "ID поставщика", example = "1", required = true)
            @PathVariable Long id) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats/count")
    @Operation(
            summary = "Получить статистику по поставщикам",
            description = "Возвращает общее количество поставщиков и другую статистику"
    )
    public ResponseEntity<Object> getSuppliersStats() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }
}