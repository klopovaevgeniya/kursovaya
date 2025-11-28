package com.example.AutoDetail.controller;

import com.example.AutoDetail.dto.CartDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart API", description = "Операции с корзиной покупок")
public class CartController {

    @GetMapping
    @Operation(
            summary = "Получить все записи корзины",
            description = "Возвращает список всех товаров в корзинах всех клиентов"
    )
    public ResponseEntity<List<CartDto>> getAllCartItems() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить запись корзины по ID",
            description = "Возвращает запись корзины по указанному идентификатору"
    )
    public ResponseEntity<CartDto> getCartItemById(
            @Parameter(description = "ID записи корзины", example = "1", required = true)
            @PathVariable Long id) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/client/{clientId}")
    @Operation(
            summary = "Получить корзину клиента",
            description = "Возвращает все товары в корзине указанного клиента"
    )
    public ResponseEntity<List<CartDto>> getCartByClientId(
            @Parameter(description = "ID клиента", example = "1", required = true)
            @PathVariable Long clientId) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/item/{itemId}")
    @Operation(
            summary = "Найти записи корзины по товару",
            description = "Возвращает все записи корзины с указанным товаром"
    )
    public ResponseEntity<List<CartDto>> getCartItemsByItemId(
            @Parameter(description = "ID товара", example = "1", required = true)
            @PathVariable Long itemId) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/client/{clientId}/total")
    @Operation(
            summary = "Получить общую стоимость корзины клиента",
            description = "Возвращает общую стоимость всех товаров в корзине клиента"
    )
    public ResponseEntity<Double> getCartTotalByClientId(
            @Parameter(description = "ID клиента", example = "1", required = true)
            @PathVariable Long clientId) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(0.0);
    }

    @GetMapping("/client/{clientId}/count")
    @Operation(
            summary = "Получить количество позиций в корзине",
            description = "Возвращает количество различных товаров в корзине клиента"
    )
    public ResponseEntity<Long> getCartItemsCountByClientId(
            @Parameter(description = "ID клиента", example = "1", required = true)
            @PathVariable Long clientId) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(0L);
    }
}