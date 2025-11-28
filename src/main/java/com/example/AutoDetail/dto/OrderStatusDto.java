package com.example.AutoDetail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO для статуса заказа")
public class OrderStatusDto {

    @Schema(description = "ID статуса заказа", example = "1")
    private Long id;

    @NotBlank(message = "Статус обязателен")
    @Schema(description = "Название статуса", example = "Обрабатывается")
    private String status;

    @Schema(description = "Комментарий к статусу", example = "Заказ находится в обработке")
    private String comment;

    @Schema(description = "Количество заказов с этим статусом", example = "15")
    private Integer ordersCount;

    // Конструкторы
    public OrderStatusDto() {}

    public OrderStatusDto(Long id, String status, String comment) {
        this.id = id;
        this.status = status;
        this.comment = comment;
    }

    public OrderStatusDto(Long id, String status, String comment, Integer ordersCount) {
        this.id = id;
        this.status = status;
        this.comment = comment;
        this.ordersCount = ordersCount;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Integer getOrdersCount() { return ordersCount; }
    public void setOrdersCount(Integer ordersCount) { this.ordersCount = ordersCount; }

    @Override
    public String toString() {
        return "OrderStatusDTO{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", comment='" + comment + '\'' +
                ", ordersCount=" + ordersCount +
                '}';
    }
}