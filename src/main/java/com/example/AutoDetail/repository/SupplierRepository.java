package com.example.AutoDetail.repository;

import com.example.AutoDetail.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    // === БАЗОВЫЕ МЕТОДЫ ПОИСКА ===

    // Поиск по названию
    List<Supplier> findByNameContainingIgnoreCase(String name);

    // Поиск по email
    List<Supplier> findByContactEmailContainingIgnoreCase(String email);

    // Поиск по телефону
    List<Supplier> findByContactPhoneContaining(String phone);

    // === УЛУЧШЕННЫЙ ПОИСК ===

    // Комбинированный поиск - ОСНОВНОЙ МЕТОД
    @Query("SELECT s FROM Supplier s WHERE " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.contactEmail) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "s.contactPhone LIKE CONCAT('%', :search, '%')")
    List<Supplier> searchSuppliers(@Param("search") String search);

    // Быстрый поиск по началу названия (для autocomplete)
    @Query("SELECT s FROM Supplier s WHERE " +
            "LOWER(s.name) LIKE LOWER(CONCAT(:searchTerm, '%')) " +
            "ORDER BY s.name ASC")
    List<Supplier> searchSuppliersStartsWith(@Param("searchTerm") String searchTerm);

    // Расширенный поиск с дополнительными полями
    @Query("SELECT s FROM Supplier s WHERE " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.contactEmail) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "s.contactPhone LIKE CONCAT('%', :searchTerm, '%') OR " +
            "LOWER(s.contactEmail) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "ORDER BY s.name ASC")
    List<Supplier> searchSuppliersExtended(@Param("searchTerm") String searchTerm);

    // Поиск поставщиков с количеством товаров
    @Query("SELECT s, COUNT(i) as itemCount FROM Supplier s LEFT JOIN s.items i " +
            "WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "GROUP BY s " +
            "ORDER BY s.name ASC")
    List<Object[]> searchSuppliersWithItemCount(@Param("searchTerm") String searchTerm);
}