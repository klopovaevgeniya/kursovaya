package com.example.AutoDetail.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO для корзины покупок")
public class CartDto {

    @Schema(description = "ID записи корзины", example = "1")
    private Long id;

    @Schema(description = "Клиент")
    private ClientDto client;

    @Schema(description = "Товар")
    private ItemDto item;

    @Schema(description = "Количество товара", example = "2")
    private Integer quantity;

    // Конструкторы
    public CartDto() {}

    public CartDto(Long id, ClientDto client, ItemDto item, Integer quantity) {
        this.id = id;
        this.client = client;
        this.item = item;
        this.quantity = quantity;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ClientDto getClient() { return client; }
    public void setClient(ClientDto client) { this.client = client; }

    public ItemDto getItem() { return item; }
    public void setItem(ItemDto item) { this.item = item; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    @Override
    public String toString() {
        return "CartDTO{" +
                "id=" + id +
                ", client=" + (client != null ? client.getId() : "null") +
                ", item=" + (item != null ? item.getId() : "null") +
                ", quantity=" + quantity +
                '}';
    }
}