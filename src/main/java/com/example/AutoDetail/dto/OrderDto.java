package com.example.AutoDetail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "DTO для заказа")
public class OrderDto {

    @Schema(description = "ID заказа", example = "1")
    private Long id;

    @NotNull(message = "Клиент обязателен")
    @Schema(description = "ID клиента", example = "1")
    private Long clientId;

    @Schema(description = "ID пользователя (менеджера)", example = "2")
    private Long userId;

    @NotNull(message = "Сумма заказа обязательна")
    @DecimalMin(value = "0.0", inclusive = false, message = "Сумма заказа должна быть больше 0")
    @Schema(description = "Общая сумма заказа", example = "12500.75")
    private Double totalAmount;

    @Schema(description = "Дата и время создания заказа", example = "2023-12-01T10:30:00")
    private LocalDateTime createdAt;

    @NotNull(message = "Статус заказа обязателен")
    @Schema(description = "ID статуса заказа", example = "1")
    private Long statusId;

    @Schema(description = "Информация о клиенте")
    private ClientDto client;

    @Schema(description = "Информация о статусе заказа")
    private OrderStatusDto orderStatus;

    // Конструкторы
    public OrderDto() {}

    public OrderDto(Long id, Long clientId, Long userId, Double totalAmount,
                    LocalDateTime createdAt, Long statusId) {
        this.id = id;
        this.clientId = clientId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.statusId = statusId;
    }

    public OrderDto(Long id, Long clientId, Long userId, Double totalAmount,
                    LocalDateTime createdAt, Long statusId, ClientDto client, OrderStatusDto orderStatus) {
        this.id = id;
        this.clientId = clientId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.statusId = statusId;
        this.client = client;
        this.orderStatus = orderStatus;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getStatusId() { return statusId; }
    public void setStatusId(Long statusId) { this.statusId = statusId; }

    public ClientDto getClient() { return client; }
    public void setClient(ClientDto client) { this.client = client; }

    public OrderStatusDto getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatusDto orderStatus) { this.orderStatus = orderStatus; }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", totalAmount=" + totalAmount +
                ", createdAt=" + createdAt +
                ", statusId=" + statusId +
                '}';
    }
}