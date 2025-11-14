package com.example.AutoDetail.repository;

import com.example.AutoDetail.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Базовые методы поиска
    List<Order> findByClientId(Long clientId);
    List<Order> findByStatusId(Long statusId);

    // Поиск заказов за период
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersByDateRange(@Param("startDate") java.time.LocalDateTime startDate,
                                      @Param("endDate") java.time.LocalDateTime endDate);

    // УЛУЧШЕННЫЙ поиск заказов
    @Query("SELECT o FROM Order o WHERE " +
            "CAST(o.id AS string) LIKE CONCAT('%', :searchTerm, '%') OR " +
            "CAST(o.clientId AS string) LIKE CONCAT('%', :searchTerm, '%') OR " +
            "CAST(o.totalAmount AS string) LIKE CONCAT('%', :searchTerm, '%')")
    List<Order> searchOrders(@Param("searchTerm") String searchTerm);

    // Поиск заказов с информацией о клиенте
    @Query("SELECT o FROM Order o WHERE o.id = :orderId")
    List<Order> findOrderById(@Param("orderId") Long orderId);
}