package com.example.AutoDetail.repository;

import com.example.AutoDetail.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // Поиск по названию
    List<Item> findByNameContainingIgnoreCase(String name);

    // Поиск по артикулу
    List<Item> findByArcticalContainingIgnoreCase(String arctical);

    // Фильтрация по цене
    @Query("SELECT i FROM Item i WHERE i.price BETWEEN :minPrice AND :maxPrice")
    List<Item> findByPriceRange(@Param("minPrice") Double minPrice,
                                @Param("maxPrice") Double maxPrice);

    // Фильтрация по наличию
    List<Item> findByQuantityGreaterThan(Integer quantity);
}
