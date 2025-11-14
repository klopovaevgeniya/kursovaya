// OrderItemDto.java
package com.example.AutoDetail.dto;

public class OrderItemDto {
    private Long itemId;
    private Integer quantity;
    private Double price;

    // Конструкторы
    public OrderItemDto() {}

    public OrderItemDto(Long itemId, Integer quantity, Double price) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.price = price;
    }

    // Геттеры и сеттеры
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}