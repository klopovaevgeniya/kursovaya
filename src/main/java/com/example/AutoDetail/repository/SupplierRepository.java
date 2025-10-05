package com.example.AutoDetail.repository;

import com.example.AutoDetail.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    // Поиск по названию
    List<Supplier> findByNameContainingIgnoreCase(String name);

    // Поиск по email
    List<Supplier> findByContactEmailContainingIgnoreCase(String email);

    // Поиск по телефону
    List<Supplier> findByContactPhoneContaining(String phone);

    // Комбинированный поиск
    @Query("SELECT s FROM Supplier s WHERE s.name LIKE %:search% OR s.contactEmail LIKE %:search% OR s.contactPhone LIKE %:search%")
    List<Supplier> searchSuppliers(@Param("search") String search);
}
