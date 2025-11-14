package com.example.AutoDetail.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Клиент обязателен")
    @Column(name = "ID_client")
    private Long clientId;

    @Column(name = "ID_user")
    private Long userId;

    @NotNull(message = "Сумма заказа обязательна")
    @jakarta.validation.constraints.DecimalMin(value = "0.0", inclusive = false, message = "Сумма заказа должна быть больше 0")
    private Double totalAmount;

    private LocalDateTime createdAt;

    @NotNull(message = "Статус заказа обязателен")
    @Column(name = "ID_status")
    private Long statusId;

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
}