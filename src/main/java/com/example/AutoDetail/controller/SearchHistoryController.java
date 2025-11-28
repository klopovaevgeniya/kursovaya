package com.example.AutoDetail.controller;

import com.example.AutoDetail.dto.SearchHistoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/search-history")
@Tag(name = "Search History API", description = "Операции с историей поиска")
public class SearchHistoryController {

    @GetMapping
    @Operation(
            summary = "Получить всю историю поиска",
            description = "Возвращает список всех поисковых запросов"
    )
    public ResponseEntity<List<SearchHistoryDto>> getAllSearchHistory() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить запись истории поиска по ID",
            description = "Возвращает запись истории поиска по указанному идентификатору"
    )
    public ResponseEntity<SearchHistoryDto> getSearchHistoryById(
            @Parameter(description = "ID записи истории поиска", example = "1", required = true)
            @PathVariable Long id) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/client/{clientId}")
    @Operation(
            summary = "Получить историю поиска клиента",
            description = "Возвращает все поисковые запросы указанного клиента"
    )
    public ResponseEntity<List<SearchHistoryDto>> getSearchHistoryByClientId(
            @Parameter(description = "ID клиента", example = "1", required = true)
            @PathVariable Long clientId) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/search/query/{query}")
    @Operation(
            summary = "Поиск по запросам",
            description = "Возвращает записи истории поиска, содержащие указанный запрос"
    )
    public ResponseEntity<List<SearchHistoryDto>> getSearchHistoryByQuery(
            @Parameter(description = "Поисковый запрос", example = "масло", required = true)
            @PathVariable String query) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/date-range")
    @Operation(
            summary = "Получить историю поиска за период",
            description = "Возвращает поисковые запросы за указанный период времени"
    )
    public ResponseEntity<List<SearchHistoryDto>> getSearchHistoryByDateRange(
            @Parameter(description = "Дата начала периода", example = "2023-12-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Дата окончания периода", example = "2023-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/recent")
    @Operation(
            summary = "Получить последние поисковые запросы",
            description = "Возвращает последние N поисковых запросов"
    )
    public ResponseEntity<List<SearchHistoryDto>> getRecentSearchHistory(
            @Parameter(description = "Количество записей", example = "20")
            @RequestParam(defaultValue = "20") Integer limit) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/client/{clientId}/recent")
    @Operation(
            summary = "Получить последние запросы клиента",
            description = "Возвращает последние поисковые запросы указанного клиента"
    )
    public ResponseEntity<List<SearchHistoryDto>> getRecentSearchHistoryByClient(
            @Parameter(description = "ID клиента", example = "1", required = true)
            @PathVariable Long clientId,
            @Parameter(description = "Количество записей", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/popular-queries")
    @Operation(
            summary = "Получить популярные поисковые запросы",
            description = "Возвращает самые популярные поисковые запросы"
    )
    public ResponseEntity<List<Object>> getPopularSearchQueries(
            @Parameter(description = "Количество запросов", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/stats/count-by-client")
    @Operation(
            summary = "Получить статистику по клиентам",
            description = "Возвращает количество поисковых запросов по каждому клиенту"
    )
    public ResponseEntity<Object> getSearchCountByClient() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats/daily")
    @Operation(
            summary = "Получить ежедневную статистику поиска",
            description = "Возвращает статистику поисковых запросов за сегодня"
    )
    public ResponseEntity<Object> getDailySearchStats() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/full-info")
    @Operation(
            summary = "Получить полную информацию о записи поиска",
            description = "Возвращает расширенную информацию о записи истории поиска"
    )
    public ResponseEntity<SearchHistoryDto> getSearchHistoryFullInfo(
            @Parameter(description = "ID записи истории поиска", example = "1", required = true)
            @PathVariable Long id) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }
}