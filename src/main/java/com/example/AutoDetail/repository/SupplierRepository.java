package com.example.AutoDetail.repository;

import com.example.AutoDetail.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    // === БАЗОВЫЕ МЕТОДЫ ПОИСКА ===

    // Поиск по названию
    List<Supplier> findByNameContainingIgnoreCase(String name);

    // Поиск по email
    List<Supplier> findByContactEmailContainingIgnoreCase(String email);

    // Поиск по телефону
    List<Supplier> findByContactPhoneContaining(String phone);

    // === НОВЫЕ МЕТОДЫ ДЛЯ ЭКСПОРТА/ИМПОРТА ===

    // Поиск поставщика по точному названию (для импорта)
    Optional<Supplier> findByName(String name);

    // Проверка существования поставщика по названию
    boolean existsByName(String name);

    // Поиск по точному email
    Optional<Supplier> findByContactEmail(String email);

    // Поиск по точному телефону
    Optional<Supplier> findByContactPhone(String phone);

    // Получение всех поставщиков отсортированных по названию
    List<Supplier> findAllByOrderByNameAsc();

    // Получение поставщиков с товарами
    @Query("SELECT s FROM Supplier s WHERE SIZE(s.items) > 0")
    List<Supplier> findSuppliersWithItems();

    // Получение поставщиков без товаров
    @Query("SELECT s FROM Supplier s WHERE SIZE(s.items) = 0")
    List<Supplier> findSuppliersWithoutItems();

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

    // === МЕТОДЫ ДЛЯ СТАТИСТИКИ ===

    // Количество поставщиков
    long count();

    // Количество поставщиков с товарами
    @Query("SELECT COUNT(s) FROM Supplier s WHERE SIZE(s.items) > 0")
    long countSuppliersWithItems();

    // Количество поставщиков без товаров
    @Query("SELECT COUNT(s) FROM Supplier s WHERE SIZE(s.items) = 0")
    long countSuppliersWithoutItems();

    // Поиск поставщиков по частичному совпадению телефона
    @Query("SELECT s FROM Supplier s WHERE s.contactPhone LIKE %:phonePart%")
    List<Supplier> findByPhonePart(@Param("phonePart") String phonePart);

    // Поиск поставщиков по домену email
    @Query("SELECT s FROM Supplier s WHERE s.contactEmail LIKE %:domain%")
    List<Supplier> findByEmailDomain(@Param("domain") String domain);

    // Получение уникальных email поставщиков
    @Query("SELECT DISTINCT s.contactEmail FROM Supplier s WHERE s.contactEmail IS NOT NULL")
    List<String> findDistinctEmails();

    // Проверка существования поставщика с таким же телефоном (исключая текущего)
    @Query("SELECT COUNT(s) > 0 FROM Supplier s WHERE s.contactPhone = :phone AND s.id <> :excludeId")
    boolean existsByContactPhoneAndIdNot(@Param("phone") String phone, @Param("excludeId") Long excludeId);

    // Проверка существования поставщика с таким же email (исключая текущего)
    @Query("SELECT COUNT(s) > 0 FROM Supplier s WHERE s.contactEmail = :email AND s.id <> :excludeId")
    boolean existsByContactEmailAndIdNot(@Param("email") String email, @Param("excludeId") Long excludeId);
}