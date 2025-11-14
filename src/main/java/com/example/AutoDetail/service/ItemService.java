package com.example.AutoDetail.service;

import com.example.AutoDetail.entity.Item;
import com.example.AutoDetail.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id).orElse(null);
    }

    public List<Item> searchItems(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllItems();
        }

        List<Item> byName = itemRepository.findByNameContainingIgnoreCase(query);
        List<Item> byArctical = itemRepository.findByArcticalContainingIgnoreCase(query);

        // Объединяем и убираем дубликаты
        byName.addAll(byArctical.stream()
                .filter(item -> !byName.contains(item))
                .collect(Collectors.toList()));

        return byName;
    }

    public List<Item> filterByCategoryId(Long categoryId) {
        if (categoryId == null) {
            return getAllItems();
        }
        return itemRepository.findByCategoryId(categoryId);
    }

    public List<Item> getItemsSorted(List<Item> items, String sortType) {
        if (sortType == null || items == null) {
            return items;
        }

        List<Item> sortedItems = new ArrayList<>(items);

        switch (sortType) {
            case "nameAsc":
                sortedItems.sort(Comparator.comparing(Item::getName, String.CASE_INSENSITIVE_ORDER));
                break;
            case "nameDesc":
                sortedItems.sort(Comparator.comparing(Item::getName, String.CASE_INSENSITIVE_ORDER).reversed());
                break;
            case "priceAsc":
                sortedItems.sort(Comparator.comparing(Item::getPrice));
                break;
            case "priceDesc":
                sortedItems.sort(Comparator.comparing(Item::getPrice).reversed());
                break;
        }

        return sortedItems;
    }

    public List<Item> getFilteredAndSortedItems(String search, Long category, String sort) {
        List<Item> items;

        if (search != null && !search.trim().isEmpty()) {
            items = searchItems(search);
        } else {
            items = getAllItems();
        }

        if (category != null) {
            items = filterByCategoryId(category);
        }

        return getItemsSorted(items, sort);
    }

    public List<Item> getRelatedItems(Item item) {
        if (item == null || item.getCategory() == null) {
            return new ArrayList<>();
        }

        return itemRepository.findByCategoryId(item.getCategory().getId())
                .stream()
                .filter(i -> !i.getId().equals(item.getId()))
                .limit(4)
                .collect(Collectors.toList());
    }

    public List<Item> getAvailableItems() {
        return itemRepository.findByQuantityGreaterThan(0);
    }

    public List<Item> getItemsBySupplier(Long supplierId) {
        return itemRepository.findBySupplierId(supplierId);
    }

    public List<Item> getItemsByPriceRange(Double minPrice, Double maxPrice) {
        if (minPrice == null && maxPrice == null) {
            return getAllItems();
        }

        minPrice = minPrice != null ? minPrice : 0.0;
        maxPrice = maxPrice != null ? maxPrice : Double.MAX_VALUE;

        return itemRepository.findByPriceRange(minPrice, maxPrice);
    }
}