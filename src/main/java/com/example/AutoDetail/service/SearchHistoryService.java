package com.example.AutoDetail.service;

import com.example.AutoDetail.entity.Client;
import com.example.AutoDetail.entity.SearchHistory;
import com.example.AutoDetail.repository.SearchHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;

    public SearchHistoryService(SearchHistoryRepository searchHistoryRepository) {
        this.searchHistoryRepository = searchHistoryRepository;
    }

    @Transactional
    public void saveSearch(Client client, String searchQuery) {
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String trimmedQuery = searchQuery.trim();

            // Проверяем, нет ли уже такого запроса
            if (!searchHistoryRepository.existsByClientIdAndSearchQuery(client.getId(), trimmedQuery)) {
                SearchHistory searchHistory = new SearchHistory(client, trimmedQuery);
                searchHistoryRepository.save(searchHistory);
            }
        }
    }

    public List<String> getSearchHistory(Long clientId) {
        List<String> allQueries = searchHistoryRepository.findSearchQueriesByClientId(clientId);

        // Убираем дубликаты и ограничиваем количество на уровне Java
        return allQueries.stream()
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
    }

    @Transactional
    public void clearSearchHistory(Long clientId) {
        // Можно реализовать при необходимости
    }
}