package com.example.AutoDetail.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "search_history")
public class SearchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "search_query", nullable = false)
    private String searchQuery;

    @Column(name = "search_date")
    private LocalDateTime searchDate;

    // Конструкторы
    public SearchHistory() {}

    public SearchHistory(Client client, String searchQuery) {
        this.client = client;
        this.searchQuery = searchQuery;
        this.searchDate = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public String getSearchQuery() { return searchQuery; }
    public void setSearchQuery(String searchQuery) { this.searchQuery = searchQuery; }

    public LocalDateTime getSearchDate() { return searchDate; }
    public void setSearchDate(LocalDateTime searchDate) { this.searchDate = searchDate; }
}