package com.example.AutoDetail.repository;

import com.example.AutoDetail.entity.User;
import com.example.AutoDetail.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // === БАЗОВЫЕ МЕТОДЫ ===

    Optional<User> findByLogin(String login);
    boolean existsByLogin(String login);

    // Поиск пользователей по роли
    List<User> findByRole(Role role);

    // Поиск по имени
    List<User> findByNameContainingIgnoreCase(String name);

    // Поиск по фамилии
    List<User> findBySurnameContainingIgnoreCase(String surname);

    // Подсчет пользователей по роли
    long countByRole(Role role);

    // === УЛУЧШЕННЫЙ ПОИСК МЕНЕДЖЕРОВ ===

    // Комбинированный поиск менеджеров (старый метод для совместимости)
    @Query("SELECT u FROM User u WHERE u.role = 'ROLE_MANAGER' AND " +
            "(LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.surname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.login) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<User> searchManagers(@Param("search") String search);

    // УЛУЧШЕННЫЙ поиск с телефоном и отчеством
    @Query("SELECT u FROM User u WHERE u.role = 'ROLE_MANAGER' AND " +
            "(LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.surname) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.login) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.patronymic) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.phone) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<User> findByRoleAndSearchTerm(@Param("searchTerm") String searchTerm);

    // Быстрый поиск по началу имени/фамилии (для autocomplete)
    @Query("SELECT u FROM User u WHERE u.role = 'ROLE_MANAGER' AND " +
            "(LOWER(u.name) LIKE LOWER(CONCAT(:searchTerm, '%')) OR " +
            "LOWER(u.surname) LIKE LOWER(CONCAT(:searchTerm, '%'))) " +
            "ORDER BY u.surname, u.name ASC")
    List<User> findManagersStartsWith(@Param("searchTerm") String searchTerm);

    // Поиск по комбинации имени и фамилии
    @Query("SELECT u FROM User u WHERE u.role = 'ROLE_MANAGER' AND " +
            "(LOWER(CONCAT(u.name, ' ', u.surname)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(CONCAT(u.surname, ' ', u.name)) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<User> findManagersByFullName(@Param("searchTerm") String searchTerm);

    // Поиск менеджеров с сортировкой по фамилии
    @Query("SELECT u FROM User u WHERE u.role = 'ROLE_MANAGER' AND " +
            "(LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.surname) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.login) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "ORDER BY u.surname ASC, u.name ASC")
    List<User> searchManagersOrdered(@Param("searchTerm") String searchTerm);
}