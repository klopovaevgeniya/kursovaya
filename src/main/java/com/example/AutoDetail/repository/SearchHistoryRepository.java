package com.example.AutoDetail.repository;

import com.example.AutoDetail.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    List<SearchHistory> findByClientIdOrderBySearchDateDesc(Long clientId);

    // ИСПРАВЛЕННЫЙ ЗАПРОС - убрал DISTINCT и ORDER BY
    @Query("SELECT sh.searchQuery FROM SearchHistory sh WHERE sh.client.id = :clientId")
    List<String> findSearchQueriesByClientId(@Param("clientId") Long clientId);

    boolean existsByClientIdAndSearchQuery(Long clientId, String searchQuery);

    void deleteByClientIdAndSearchQuery(Long clientId, String searchQuery);
}