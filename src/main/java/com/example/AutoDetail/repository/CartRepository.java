package com.example.AutoDetail.repository;

import com.example.AutoDetail.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // Найти все товары в корзине клиента с загрузкой связанных данных
    @Query("SELECT c FROM Cart c JOIN FETCH c.item i LEFT JOIN FETCH i.category WHERE c.client.id = :clientId")
    List<Cart> findByClientId(@Param("clientId") Long clientId);

    // Найти конкретный товар в корзине клиента
    @Query("SELECT c FROM Cart c WHERE c.client.id = :clientId AND c.item.id = :itemId")
    Optional<Cart> findByClientIdAndItemId(@Param("clientId") Long clientId, @Param("itemId") Long itemId);

    // Проверить существование товара в корзине клиента
    @Query("SELECT COUNT(c) > 0 FROM Cart c WHERE c.client.id = :clientId AND c.item.id = :itemId")
    boolean existsByClientIdAndItemId(@Param("clientId") Long clientId, @Param("itemId") Long itemId);

    // Удалить все товары из корзины клиента
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.client.id = :clientId")
    void deleteByClientId(@Param("clientId") Long clientId);

    // Удалить конкретный товар из корзины клиента
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.client.id = :clientId AND c.item.id = :itemId")
    void deleteByClientIdAndItemId(@Param("clientId") Long clientId, @Param("itemId") Long itemId);

    // Подсчитать количество товаров в корзине клиента
    @Query("SELECT COUNT(c) FROM Cart c WHERE c.client.id = :clientId")
    Long countByClientId(@Param("clientId") Long clientId);

    // Сумма количества всех товаров в корзине клиента
    @Query("SELECT SUM(c.quantity) FROM Cart c WHERE c.client.id = :clientId")
    Integer sumQuantityByClientId(@Param("clientId") Long clientId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.item.id = :itemId")
    void deleteByItemId(@Param("itemId") Long itemId);
}