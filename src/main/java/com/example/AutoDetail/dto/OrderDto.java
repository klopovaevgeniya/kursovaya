package com.example.AutoDetail.dto;

import java.time.LocalDateTime;

public class OrderDto {
    private Long id;
    private Long clientId;
    private String clientName;
    private Long userId;
    private String userName;
    private Double totalAmount;
    private LocalDateTime createdAt;
    private Long statusId;
    private String statusName;

    // Конструкторы, геттеры и сеттеры
    public OrderDto() {}

    public OrderDto(Long id, Long clientId, String clientName, Long userId, String userName,
                    Double totalAmount, LocalDateTime createdAt, Long statusId, String statusName) {
        this.id = id;
        this.clientId = clientId;
        this.clientName = clientName;
        this.userId = userId;
        this.userName = userName;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.statusId = statusId;
        this.statusName = statusName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Long getStatusId() { return statusId; }
    public void setStatusId(Long statusId) { this.statusId = statusId; }
    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }
}