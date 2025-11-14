package com.example.AutoDetail.service;

import com.example.AutoDetail.entity.*;
import com.example.AutoDetail.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdminService {

    private final ItemRepository itemRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(ItemRepository itemRepository,
                        SupplierRepository supplierRepository,
                        UserRepository userRepository,
                        CategoryRepository categoryRepository,
                        CartRepository cartRepository,
                        PasswordEncoder passwordEncoder) {
        this.itemRepository = itemRepository;
        this.supplierRepository = supplierRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // === ТОВАРЫ ===
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    public Item saveItem(Item item, Long supplierId, Long categoryId) {
        try {
            // Находим поставщика
            Optional<Supplier> supplierOpt = supplierRepository.findById(supplierId);
            if (supplierOpt.isPresent()) {
                item.setSupplier(supplierOpt.get());
            } else {
                throw new RuntimeException("Поставщик не найден с ID: " + supplierId);
            }

            // Находим категорию
            if (categoryId != null) {
                Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
                if (categoryOpt.isPresent()) {
                    item.setCategory(categoryOpt.get());
                } else {
                    throw new RuntimeException("Категория не найдена с ID: " + categoryId);
                }
            }

            // Убедимся, что isArticulGenerated не null
            if (item.getIsArticulGenerated() == null) {
                item.setIsArticulGenerated(false);
            }

            // Генерация артикула для нового товара
            if (item.getId() == null && (item.getArctical() == null || item.getArctical().trim().isEmpty())) {
                String generatedArticul = generateUniqueArticul();
                item.setArctical(generatedArticul);
                item.setIsArticulGenerated(true);
            }

            // Для существующего товара - проверяем, можно ли изменять артикул
            if (item.getId() != null) {
                Optional<Item> existingItem = itemRepository.findById(item.getId());
                if (existingItem.isPresent()) {
                    Item existing = existingItem.get();
                    // Безопасная проверка isArticulGenerated
                    Boolean isGenerated = existing.getIsArticulGenerated();
                    if (isGenerated != null && isGenerated) {
                        // Если артикул был сгенерирован, сохраняем оригинальный
                        item.setArctical(existing.getArctical());
                        item.setIsArticulGenerated(true);
                    }
                }
            }

            return itemRepository.save(item);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сохранения товара: " + e.getMessage(), e);
        }
    }

    // Перегруженный метод для сохранения с URL изображения
    public Item saveItemWithImageUrl(Item item, Long supplierId, Long categoryId, String imageUrl) {
        try {
            // Устанавливаем изображение из URL, если оно предоставлено
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                item.setImage(imageUrl);
            }

            // Убедимся, что isArticulGenerated не null
            if (item.getIsArticulGenerated() == null) {
                item.setIsArticulGenerated(false);
            }

            // Сохраняем товар
            return saveItem(item, supplierId, categoryId);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сохранения товара с изображением: " + e.getMessage(), e);
        }
    }

    // Метод для генерации уникального артикула
    private String generateUniqueArticul() {
        String articul;
        boolean isUnique = false;
        int attempts = 0;

        do {
            // Генерируем артикул в формате: AUTO-{timestamp}-{random}
            String timestamp = String.valueOf(System.currentTimeMillis());
            String random = String.valueOf((int)(Math.random() * 1000));
            articul = "AUTO-" + timestamp.substring(timestamp.length() - 6) + "-" + random;

            // Проверяем уникальность
            List<Item> existingItems = itemRepository.findByArcticalContainingIgnoreCase(articul);
            isUnique = existingItems.isEmpty();
            attempts++;

            if (attempts > 10) {
                // Альтернативный метод генерации
                articul = "AUTO-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
                List<Item> finalCheck = itemRepository.findByArcticalContainingIgnoreCase(articul);
                if (finalCheck.isEmpty()) {
                    isUnique = true;
                }
            }

            if (attempts > 15) {
                throw new RuntimeException("Не удалось сгенерировать уникальный артикул");
            }
        } while (!isUnique);

        return articul;
    }

    public void deleteItem(Long id) {
        try {
            Optional<Item> itemOpt = itemRepository.findById(id);
            if (itemOpt.isPresent()) {
                // Сначала удаляем все записи корзины, связанные с этим товаром
                if (cartRepository != null) {
                    cartRepository.deleteByItemId(id);
                }

                // Теперь удаляем сам товар
                itemRepository.deleteById(id);
            } else {
                throw new RuntimeException("Товар не найден с ID: " + id);
            }
        } catch (Exception e) {
            // Очищаем сообщение об ошибке от специальных символов для безопасного редиректа
            String cleanErrorMessage = e.getMessage().replaceAll("[\\r\\n]", " ").trim();
            throw new RuntimeException("Ошибка удаления товара: " + cleanErrorMessage, e);
        }
    }

    // === УЛУЧШЕННЫЙ ПОИСК ТОВАРОВ ===
    public List<Item> searchItemsByName(String name) {
        return itemRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Item> searchItems(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return itemRepository.findAll();
        }
        String cleanSearchTerm = searchTerm.trim();
        return itemRepository.searchItems(cleanSearchTerm);
    }

    public List<Item> searchItemsWithDetails(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return itemRepository.findAll();
        }
        String cleanSearchTerm = searchTerm.trim();
        return itemRepository.searchItemsWithDetails(cleanSearchTerm);
    }

    public List<Item> searchItemsOrderByName(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return itemRepository.findAll();
        }
        String cleanSearchTerm = searchTerm.trim();
        return itemRepository.searchItemsOrderByName(cleanSearchTerm);
    }

    // Быстрый поиск по началу строки (для autocomplete)
    public List<Item> searchItemsStartsWith(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }
        return itemRepository.searchItemsStartsWith(searchTerm.trim());
    }

    // === ПОСТАВЩИКИ ===
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Optional<Supplier> getSupplierById(Long id) {
        return supplierRepository.findById(id);
    }

    public Supplier saveSupplier(Supplier supplier) {
        try {
            return supplierRepository.save(supplier);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сохранения поставщика: " + e.getMessage(), e);
        }
    }

    public void deleteSupplier(Long id) {
        try {
            // Проверяем, есть ли товары у этого поставщика
            Optional<Supplier> supplierOpt = supplierRepository.findById(id);
            if (supplierOpt.isPresent()) {
                Supplier supplier = supplierOpt.get();
                if (!supplier.getItems().isEmpty()) {
                    throw new RuntimeException("Нельзя удалить поставщика, у которого есть товары. Сначала удалите или переназначьте товары.");
                }
                supplierRepository.deleteById(id);
            } else {
                throw new RuntimeException("Поставщик не найден с ID: " + id);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка удаления поставщика: " + e.getMessage(), e);
        }
    }

    public List<Supplier> searchSuppliers(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return supplierRepository.findAll();
        }
        String cleanSearchTerm = searchTerm.trim();
        return supplierRepository.searchSuppliers(cleanSearchTerm);
    }

    public List<Supplier> searchSuppliersExtended(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return supplierRepository.findAll();
        }
        String cleanSearchTerm = searchTerm.trim();
        return supplierRepository.searchSuppliersExtended(cleanSearchTerm);
    }

    // Быстрый поиск поставщиков по началу названия
    public List<Supplier> searchSuppliersStartsWith(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }
        return supplierRepository.searchSuppliersStartsWith(searchTerm.trim());
    }

    // === КАТЕГОРИИ ===
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category saveCategory(Category category) {
        try {
            // Проверка на уникальность имени
            if (category.getId() == null) { // Новая категория
                if (categoryRepository.existsByName(category.getName())) {
                    throw new RuntimeException("Категория с таким названием уже существует");
                }
            } else { // Редактирование существующей категории
                Optional<Category> existingCategory = categoryRepository.findByName(category.getName());
                if (existingCategory.isPresent() && !existingCategory.get().getId().equals(category.getId())) {
                    throw new RuntimeException("Категория с таким названием уже существует");
                }
            }

            return categoryRepository.save(category);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сохранения категории: " + e.getMessage(), e);
        }
    }

    public void deleteCategory(Long id) {
        try {
            // Проверяем, есть ли товары в этой категории
            Optional<Category> categoryOpt = categoryRepository.findById(id);
            if (categoryOpt.isPresent()) {
                Category category = categoryOpt.get();
                if (!category.getItems().isEmpty()) {
                    throw new RuntimeException("Нельзя удалить категорию, в которой есть товары. Сначала удалите или переназначьте товары.");
                }
                categoryRepository.deleteById(id);
            } else {
                throw new RuntimeException("Категория не найдена с ID: " + id);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка удаления категории: " + e.getMessage(), e);
        }
    }

    public List<Category> searchCategories(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return categoryRepository.findAll();
        }
        String cleanSearchTerm = searchTerm.trim();
        return categoryRepository.searchCategories(cleanSearchTerm);
    }

    public List<Category> searchCategoriesOrderByName(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return categoryRepository.findAll();
        }
        String cleanSearchTerm = searchTerm.trim();
        return categoryRepository.searchCategoriesOrderByName(cleanSearchTerm);
    }

    // Быстрый поиск категорий по началу названия
    public List<Category> searchCategoriesStartsWith(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }
        return categoryRepository.searchCategoriesStartsWith(searchTerm.trim());
    }

    // === МЕНЕДЖЕРЫ ===
    public List<User> getAllManagers() {
        return userRepository.findByRole(Role.ROLE_MANAGER);
    }

    public Optional<User> getManagerById(Long id) {
        return userRepository.findById(id);
    }

    public User saveManager(User manager) {
        try {
            // Шифруем пароль если он новый
            if (manager.getPassword() != null && !manager.getPassword().startsWith("$2a$")) {
                manager.setPassword(passwordEncoder.encode(manager.getPassword()));
            }

            manager.setRole(Role.ROLE_MANAGER);
            return userRepository.save(manager);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сохранения менеджера: " + e.getMessage(), e);
        }
    }

    public void deleteManager(Long id) {
        try {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
            } else {
                throw new RuntimeException("Менеджер не найден с ID: " + id);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка удаления менеджера: " + e.getMessage(), e);
        }
    }

    // === УЛУЧШЕННЫЙ ПОИСК МЕНЕДЖЕРОВ ===
    public List<User> searchManagers(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return userRepository.findByRole(Role.ROLE_MANAGER);
        }
        String cleanSearchTerm = searchTerm.trim();
        return userRepository.findByRoleAndSearchTerm(cleanSearchTerm);
    }

    public List<User> searchManagersOrdered(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return userRepository.findByRole(Role.ROLE_MANAGER);
        }
        String cleanSearchTerm = searchTerm.trim();
        return userRepository.searchManagersOrdered(cleanSearchTerm);
    }

    public List<User> findManagersByFullName(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return userRepository.findByRole(Role.ROLE_MANAGER);
        }
        String cleanSearchTerm = searchTerm.trim();
        return userRepository.findManagersByFullName(cleanSearchTerm);
    }

    // Быстрый поиск менеджеров по началу имени/фамилии
    public List<User> searchManagersStartsWith(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }
        return userRepository.findManagersStartsWith(searchTerm.trim());
    }

    public boolean isLoginExists(String login, Long excludeId) {
        Optional<User> existingUser = userRepository.findByLogin(login);
        if (existingUser.isPresent()) {
            if (excludeId == null) {
                return true;
            }
            return !existingUser.get().getId().equals(excludeId);
        }
        return false;
    }

    // === СТАТИСТИКА ===
    public long getTotalItems() {
        return itemRepository.count();
    }

    public long getTotalSuppliers() {
        return supplierRepository.count();
    }

    public long getTotalManagers() {
        return userRepository.countByRole(Role.ROLE_MANAGER);
    }

    public long getTotalClients() {
        return userRepository.countByRole(Role.ROLE_CLIENT);
    }

    public long getTotalCategories() {
        return categoryRepository.count();
    }

    // === ДОПОЛНИТЕЛЬНЫЕ МЕТОДЫ ДЛЯ ФИЛЬТРАЦИИ ===
    public List<Item> getItemsByPriceRange(Double minPrice, Double maxPrice) {
        return itemRepository.findByPriceRange(minPrice, maxPrice);
    }

    public List<Item> getItemsInStock() {
        return itemRepository.findByQuantityGreaterThan(0);
    }

    public List<Item> getItemsBySupplier(Long supplierId) {
        return itemRepository.findBySupplierId(supplierId);
    }

    public List<Item> getItemsByCategory(Long categoryId) {
        return itemRepository.findByCategoryId(categoryId);
    }

    public List<Item> getItemsByCategoryName(String categoryName) {
        return itemRepository.findByCategoryName(categoryName);
    }

    // === МЕТОДЫ ДЛЯ ПОЛУЧЕНИЯ СТАТИСТИКИ С ПОИСКОМ ===
    public long getSearchItemsCount(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return itemRepository.count();
        }
        return searchItems(searchTerm).size();
    }

    public long getSearchSuppliersCount(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return supplierRepository.count();
        }
        return searchSuppliers(searchTerm).size();
    }

    public long getSearchManagersCount(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return userRepository.countByRole(Role.ROLE_MANAGER);
        }
        return searchManagers(searchTerm).size();
    }

    public long getSearchCategoriesCount(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return categoryRepository.count();
        }
        return searchCategories(searchTerm).size();
    }

    // === МЕТОДЫ ДЛЯ ПРОВЕРКИ ДУБЛИКАТОВ ===
    public boolean isSupplierNameExists(String name, Long excludeId) {
        List<Supplier> suppliers = supplierRepository.findByNameContainingIgnoreCase(name);
        if (suppliers.isEmpty()) {
            return false;
        }
        if (excludeId == null) {
            return true;
        }
        return suppliers.stream().anyMatch(s -> !s.getId().equals(excludeId));
    }

    public boolean isCategoryNameExists(String name, Long excludeId) {
        Optional<Category> categoryOpt = categoryRepository.findByName(name);
        if (categoryOpt.isEmpty()) {
            return false;
        }
        if (excludeId == null) {
            return true;
        }
        return !categoryOpt.get().getId().equals(excludeId);
    }
}