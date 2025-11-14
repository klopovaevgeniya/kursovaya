package com.example.AutoDetail.repository;

import com.example.AutoDetail.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // === БАЗОВЫЕ МЕТОДЫ ===

    Optional<Category> findByName(String name);
    List<Category> findByNameContainingIgnoreCase(String name);
    boolean existsByName(String name);

    // === УЛУЧШЕННЫЙ ПОИСК ===

    // Основной метод поиска категорий
    @Query("SELECT c FROM Category c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Category> searchCategories(@Param("searchTerm") String searchTerm);

    // Быстрый поиск по началу названия (для autocomplete)
    @Query("SELECT c FROM Category c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT(:searchTerm, '%')) " +
            "ORDER BY c.name ASC")
    List<Category> searchCategoriesStartsWith(@Param("searchTerm") String searchTerm);

    // Поиск категорий с количеством товаров
    @Query("SELECT c, COUNT(i) as itemCount FROM Category c LEFT JOIN c.items i " +
            "WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "GROUP BY c " +
            "ORDER BY c.name ASC")
    List<Object[]> searchCategoriesWithItemCount(@Param("searchTerm") String searchTerm);

    // Поиск категорий с сортировкой по названию
    @Query("SELECT c FROM Category c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "ORDER BY c.name ASC")
    List<Category> searchCategoriesOrderByName(@Param("searchTerm") String searchTerm);

    // Поиск категорий с товарами (для выпадающих списков)
    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.items WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "ORDER BY c.name ASC")
    List<Category> searchCategoriesWithItems(@Param("searchTerm") String searchTerm);
}