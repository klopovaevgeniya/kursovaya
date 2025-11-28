package com.example.AutoDetail.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "DTO для истории поиска")
public class SearchHistoryDto {

    @Schema(description = "ID записи истории поиска", example = "1")
    private Long id;

    @Schema(description = "ID клиента", example = "1")
    private Long clientId;

    @Schema(description = "Поисковый запрос", example = "моторное масло 5w30")
    private String searchQuery;

    @Schema(description = "Дата и время поиска", example = "2023-12-01T10:30:00")
    private LocalDateTime searchDate;

    @Schema(description = "Информация о клиенте")
    private ClientDto client;

    // Конструкторы
    public SearchHistoryDto() {}

    public SearchHistoryDto(Long id, Long clientId, String searchQuery, LocalDateTime searchDate) {
        this.id = id;
        this.clientId = clientId;
        this.searchQuery = searchQuery;
        this.searchDate = searchDate;
    }

    public SearchHistoryDto(Long id, Long clientId, String searchQuery, LocalDateTime searchDate, ClientDto client) {
        this.id = id;
        this.clientId = clientId;
        this.searchQuery = searchQuery;
        this.searchDate = searchDate;
        this.client = client;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public String getSearchQuery() { return searchQuery; }
    public void setSearchQuery(String searchQuery) { this.searchQuery = searchQuery; }

    public LocalDateTime getSearchDate() { return searchDate; }
    public void setSearchDate(LocalDateTime searchDate) { this.searchDate = searchDate; }

    public ClientDto getClient() { return client; }
    public void setClient(ClientDto client) { this.client = client; }

    @Override
    public String toString() {
        return "SearchHistoryDTO{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", searchQuery='" + searchQuery + '\'' +
                ", searchDate=" + searchDate +
                '}';
    }
}