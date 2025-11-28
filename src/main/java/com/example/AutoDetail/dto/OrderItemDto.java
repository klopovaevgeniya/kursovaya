package com.example.AutoDetail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "DTO для позиции заказа")
public class OrderItemDto {

    @Schema(description = "ID позиции заказа", example = "1")
    private Long id;

    @NotNull(message = "Заказ обязателен")
    @Schema(description = "ID заказа", example = "1")
    private Long orderId;

    @NotNull(message = "Товар обязателен")
    @Schema(description = "ID товара", example = "1")
    private Long itemId;

    @NotNull(message = "Количество обязательно")
    @Positive(message = "Количество должно быть положительным")
    @Schema(description = "Количество товара", example = "2")
    private Integer quantity;

    @NotNull(message = "Цена обязательна")
    @Positive(message = "Цена должна быть положительной")
    @Schema(description = "Цена за единицу товара", example = "2500.50")
    private Double price;

    @Schema(description = "Информация о заказе")
    private OrderDto order;

    @Schema(description = "Информация о товаре")
    private ItemDto item;

    // Конструкторы
    public OrderItemDto() {}

    public OrderItemDto(Long id, Long orderId, Long itemId, Integer quantity, Double price) {
        this.id = id;
        this.orderId = orderId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.price = price;
    }

    public OrderItemDto(Long id, Long orderId, Long itemId, Integer quantity, Double price,
                        OrderDto order, ItemDto item) {
        this.id = id;
        this.orderId = orderId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.price = price;
        this.order = order;
        this.item = item;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public OrderDto getOrder() { return order; }
    public void setOrder(OrderDto order) { this.order = order; }

    public ItemDto getItem() { return item; }
    public void setItem(ItemDto item) { this.item = item; }

    // Дополнительные методы для удобства
    @Schema(description = "Общая стоимость позиции", example = "5001.00")
    public Double getTotalPrice() {
        return price * quantity;
    }

    @Override
    public String toString() {
        return "OrderItemDto{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", itemId=" + itemId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", totalPrice=" + getTotalPrice() +
                '}';
    }
}