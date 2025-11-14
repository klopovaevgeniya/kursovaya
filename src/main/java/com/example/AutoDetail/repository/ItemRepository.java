package com.example.AutoDetail.repository;

import com.example.AutoDetail.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // === БАЗОВЫЕ МЕТОДЫ ПОИСКА ===

    // Поиск по названию
    List<Item> findByNameContainingIgnoreCase(String name);

    // Поиск по артикулу
    List<Item> findByArcticalContainingIgnoreCase(String arctical);

    // Фильтрация по цене
    @Query("SELECT i FROM Item i WHERE i.price BETWEEN :minPrice AND :maxPrice")
    List<Item> findByPriceRange(@Param("minPrice") Double minPrice,
                                @Param("maxPrice") Double maxPrice);

    // Фильтрация по наличию
    List<Item> findByQuantityGreaterThan(Integer quantity);

    // Поиск по поставщику
    List<Item> findBySupplierId(Long supplierId);

    // Поиск по категории
    List<Item> findByCategoryId(Long categoryId);
    List<Item> findByCategoryName(String categoryName);

    // === УЛУЧШЕННЫЙ ПОИСК ДЛЯ АДМИН-ПАНЕЛИ ===

    // Полнотекстовый поиск по всем полям
    @Query("SELECT i FROM Item i WHERE " +
            "LOWER(i.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(i.arctical) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(CAST(i.price AS string)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Item> searchItems(@Param("searchTerm") String searchTerm);

    // Быстрый поиск по началу строки (для autocomplete)
    @Query("SELECT i FROM Item i WHERE " +
            "LOWER(i.name) LIKE LOWER(CONCAT(:searchTerm, '%')) OR " +
            "LOWER(i.arctical) LIKE LOWER(CONCAT(:searchTerm, '%'))")
    List<Item> searchItemsStartsWith(@Param("searchTerm") String searchTerm);

    // Поиск по названию и артикулу с сортировкой
    @Query("SELECT i FROM Item i WHERE " +
            "LOWER(i.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(i.arctical) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "ORDER BY i.name ASC")
    List<Item> searchItemsOrderByName(@Param("searchTerm") String searchTerm);

    // Поиск товаров с информацией о поставщике и категории
    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.supplier LEFT JOIN FETCH i.category WHERE " +
            "LOWER(i.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(i.arctical) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(i.supplier.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(i.category.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Item> searchItemsWithDetails(@Param("searchTerm") String searchTerm);
}