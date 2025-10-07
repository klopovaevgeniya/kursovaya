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
    Optional<User> findByLogin(String login);
    boolean existsByLogin(String login);

    // Поиск пользователей по роли
    List<User> findByRole(Role role);

    // Поиск по имени
    List<User> findByNameContainingIgnoreCase(String name);

    // Поиск по фамилии
    List<User> findBySurnameContainingIgnoreCase(String surname);

    // Комбинированный поиск менеджеров
    @Query("SELECT u FROM User u WHERE u.role = 'ROLE_MANAGER' AND (u.name LIKE %:search% OR u.surname LIKE %:search% OR u.login LIKE %:search%)")
    List<User> searchManagers(@Param("search") String search);

    // Подсчет пользователей по роли
    long countByRole(Role role);
}