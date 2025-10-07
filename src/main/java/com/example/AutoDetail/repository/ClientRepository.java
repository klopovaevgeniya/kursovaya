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

    // Поиск по имени
    List<Client> findByNameContainingIgnoreCase(String name);

    // Поиск по фамилии
    List<Client> findBySurnameContainingIgnoreCase(String surname);

    // Поиск по телефону
    List<Client> findByPhoneContaining(String phone);

    // Комбинированный поиск
    @Query("SELECT c FROM Client c WHERE c.name LIKE %:search% OR c.surname LIKE %:search% OR c.phone LIKE %:search%")
    List<Client> searchClients(@Param("search") String search);
}