package com.example.AutoDetail.repository;

import com.example.AutoDetail.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByLogin(String login);
    boolean existsByLogin(String login);
    boolean existsByPhone(String phone);
    Optional<Client> findByEmail(String email);
    boolean existsByEmail(String email);

    // Базовые методы поиска
    List<Client> findByNameContainingIgnoreCase(String name);
    List<Client> findBySurnameContainingIgnoreCase(String surname);
    List<Client> findByPhoneContaining(String phone);

    // Комбинированный поиск (старый метод для совместимости)
    @Query("SELECT c FROM Client c WHERE c.name LIKE %:search% OR c.surname LIKE %:search% OR c.phone LIKE %:search%")
    List<Client> searchClients(@Param("search") String search);

    // УЛУЧШЕННЫЙ поиск клиентов
    @Query("SELECT c FROM Client c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.surname) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.patronymic) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.phone) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.login) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Client> searchClientsExtended(@Param("searchTerm") String searchTerm);

    // Быстрый поиск по началу имени/фамилии (для autocomplete)
    @Query("SELECT c FROM Client c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT(:searchTerm, '%')) OR " +
            "LOWER(c.surname) LIKE LOWER(CONCAT(:searchTerm, '%')) " +
            "ORDER BY c.surname, c.name ASC")
    List<Client> searchClientsStartsWith(@Param("searchTerm") String searchTerm);

    // Поиск по комбинации имени и фамилии
    @Query("SELECT c FROM Client c WHERE " +
            "LOWER(CONCAT(c.name, ' ', c.surname)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(CONCAT(c.surname, ' ', c.name)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Client> findClientsByFullName(@Param("searchTerm") String searchTerm);
}