package com.example.AutoDetail.repository;

import com.example.AutoDetail.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Поиск по ID клиента
    List<Order> findByClientId(Long clientId);

    // Поиск по статусу
    List<Order> findByStatusId(Long statusId);

    // Поиск заказов за период
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersByDateRange(@Param("startDate") java.time.LocalDateTime startDate,
                                      @Param("endDate") java.time.LocalDateTime endDate);
}