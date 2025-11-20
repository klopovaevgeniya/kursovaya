package com.example.AutoDetail.service;

import com.example.AutoDetail.controller.client.ClientController;
import com.example.AutoDetail.entity.Cart;
import com.example.AutoDetail.entity.Client;
import com.example.AutoDetail.entity.Item;
import com.example.AutoDetail.repository.CartRepository;
import com.example.AutoDetail.repository.ClientRepository;
import com.example.AutoDetail.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    private final CartRepository cartRepository;
    private final ClientRepository clientRepository;
    private final ItemRepository itemRepository;

    // Константы для валидации
    private static final int MAX_QUANTITY_PER_ITEM = 1000;
    private static final int MAX_TOTAL_ITEMS_IN_CART = 50;

    public CartService(CartRepository cartRepository,
                       ClientRepository clientRepository,
                       ItemRepository itemRepository) {
        this.cartRepository = cartRepository;
        this.clientRepository = clientRepository;
        this.itemRepository = itemRepository;
    }

    public List<Cart> getCartByClientId(Long clientId) {
        logger.debug("Получение корзины для клиента ID: {}", clientId);

        // Валидация ID клиента
        validateClientId(clientId);

        if (!clientRepository.existsById(clientId)) {
            throw new RuntimeException("Клиент с ID " + clientId + " не найден");
        }

        List<Cart> cartItems = cartRepository.findByClientId(clientId);
        logger.debug("Найдено {} товаров в корзине клиента ID: {}", cartItems.size(), clientId);
        return cartItems;
    }

    public Long countItemsInCart(Long clientId) {
        logger.debug("Подсчет количества товаров в корзине для клиента ID: {}", clientId);

        // Валидация ID клиента
        validateClientId(clientId);

        if (!clientRepository.existsById(clientId)) {
            throw new RuntimeException("Клиент с ID " + clientId + " не найден");
        }

        Long count = cartRepository.countByClientId(clientId);
        logger.debug("Количество товаров в корзине клиента ID {}: {}", clientId, count);
        return count;
    }

    public int getTotalItemsCount(Long clientId) {
        logger.debug("Подсчет общего количества единиц товара для клиента ID: {}", clientId);

        // Валидация ID клиента
        validateClientId(clientId);

        if (!clientRepository.existsById(clientId)) {
            throw new RuntimeException("Клиент с ID " + clientId + " не найден");
        }

        List<Cart> cartItems = cartRepository.findByClientId(clientId);
        int totalCount = cartItems.stream()
                .mapToInt(Cart::getQuantity)
                .sum();
        logger.debug("Общее количество единиц товара в корзине клиента ID {}: {}", clientId, totalCount);
        return totalCount;
    }

    @Transactional
    public void addToCart(Long clientId, Long itemId, int quantity) {
        logger.info("Добавление товара ID: {} в корзину клиента ID: {}, количество: {}", itemId, clientId, quantity);

        // Валидация входных данных
        validateCartData(clientId, itemId, quantity);

        // Проверяем существование клиента
        if (!clientRepository.existsById(clientId)) {
            throw new RuntimeException("Клиент с ID " + clientId + " не найден");
        }

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));


        Optional<Cart> existingCartItem = cartRepository.findByClientIdAndItemId(clientId, itemId);

        if (existingCartItem.isPresent()) {
            // Обновляем существующий товар в корзине
            Cart cart = existingCartItem.get();
            int newQuantity = cart.getQuantity() + quantity;

            // Проверяем максимальное количество на товар
            validateMaxQuantityPerItem(newQuantity);

            cart.setQuantity(newQuantity);
            cartRepository.save(cart);
            logger.info("Обновлено количество товара ID: {} в корзине клиента ID: {} на {}",
                    itemId, clientId, newQuantity);
        } else {
            // Проверяем общее количество товаров в корзине
            validateTotalItemsInCart(clientId);

            // Проверяем максимальное количество на товар
            validateMaxQuantityPerItem(quantity);

            // Добавляем новый товар в корзину
            Cart newCartItem = new Cart(client, item, quantity);
            cartRepository.save(newCartItem);
            logger.info("Добавлен новый товар ID: {} в корзину клиента ID: {}, количество: {}",
                    itemId, clientId, quantity);
        }
    }

    @Transactional
    public void updateQuantity(Long clientId, Long itemId, int quantity) {
        logger.info("Обновление количества товара ID: {} в корзине клиента ID: {} на {}",
                itemId, clientId, quantity);

        // Валидация входных данных
        validateCartData(clientId, itemId, quantity);

        if (!clientRepository.existsById(clientId)) {
            throw new RuntimeException("Клиент с ID " + clientId + " не найден");
        }

        if (quantity <= 0) {
            removeItem(clientId, itemId);
            return;
        }

        Cart cartItem = cartRepository.findByClientIdAndItemId(clientId, itemId)
                .orElseThrow(() -> new RuntimeException("Товар не найден в корзине"));

        Item item = cartItem.getItem();

        // Проверяем максимальное количество на товар
        validateMaxQuantityPerItem(quantity);

        cartItem.setQuantity(quantity);
        cartRepository.save(cartItem);
        logger.info("Количество товара ID: {} в корзине клиента ID: {} обновлено на {}",
                itemId, clientId, quantity);
    }

    @Transactional
    public void removeItem(Long clientId, Long itemId) {
        logger.info("Удаление товара ID: {} из корзины клиента ID: {}", itemId, clientId);

        // Валидация входных данных
        validateClientId(clientId);
        validateItemId(itemId);

        if (!clientRepository.existsById(clientId)) {
            throw new RuntimeException("Клиент с ID " + clientId + " не найден");
        }

        if (!cartRepository.existsByClientIdAndItemId(clientId, itemId)) {
            throw new RuntimeException("Товар не найден в корзине клиента");
        }

        cartRepository.deleteByClientIdAndItemId(clientId, itemId);
        logger.info("Товар ID: {} удален из корзины клиента ID: {}", itemId, clientId);
    }

    @Transactional
    public void clearCart(Long clientId) {
        logger.info("Очистка корзины клиента ID: {}", clientId);

        // Валидация ID клиента
        validateClientId(clientId);

        if (!clientRepository.existsById(clientId)) {
            throw new RuntimeException("Клиент с ID " + clientId + " не найден");
        }

        Long itemsCount = cartRepository.countByClientId(clientId);
        cartRepository.deleteByClientId(clientId);
        logger.info("Корзина клиента ID: {} очищена. Удалено {} товаров", clientId, itemsCount);
    }

    public double getCartTotal(Long clientId) {
        logger.debug("Расчет общей суммы корзины для клиента ID: {}", clientId);

        // Валидация ID клиента
        validateClientId(clientId);

        if (!clientRepository.existsById(clientId)) {
            throw new RuntimeException("Клиент с ID " + clientId + " не найден");
        }

        List<Cart> cartItems = cartRepository.findByClientId(clientId);
        double total = cartItems.stream()
                .mapToDouble(c -> c.getItem().getPrice() * c.getQuantity())
                .sum();
        logger.debug("Общая сумма корзины клиента ID {}: {}", clientId, total);
        return total;
    }

    public ClientController.CartSummary getCartSummary(Long clientId) {
        logger.debug("Получение сводки корзины для клиента ID: {}", clientId);

        // Валидация ID клиента
        validateClientId(clientId);

        if (!clientRepository.existsById(clientId)) {
            throw new RuntimeException("Клиент с ID " + clientId + " не найден");
        }

        List<Cart> cartItems = getCartByClientId(clientId);

        double totalSum = cartItems.stream()
                .mapToDouble(c -> c.getItem().getPrice() * c.getQuantity())
                .sum();

        int totalItems = cartItems.stream()
                .mapToInt(Cart::getQuantity)
                .sum();

        long cartItemsCount = countItemsInCart(clientId);

        logger.debug("Сводка корзины клиента ID {}: товаров - {}, единиц - {}, сумма - {}",
                clientId, cartItemsCount, totalItems, totalSum);

        return new ClientController.CartSummary(cartItems, totalSum, totalItems, cartItemsCount);
    }

    public boolean isCartEmpty(Long clientId) {
        logger.debug("Проверка пустоты корзины для клиента ID: {}", clientId);

        // Валидация ID клиента
        validateClientId(clientId);

        if (!clientRepository.existsById(clientId)) {
            throw new RuntimeException("Клиент с ID " + clientId + " не найден");
        }

        boolean isEmpty = cartRepository.countByClientId(clientId) == 0;
        logger.debug("Корзина клиента ID {} пуста: {}", clientId, isEmpty);
        return isEmpty;
    }

    public Optional<Cart> getCartItem(Long clientId, Long itemId) {
        logger.debug("Получение конкретного товара ID: {} из корзины клиента ID: {}", itemId, clientId);

        // Валидация входных данных
        validateClientId(clientId);
        validateItemId(itemId);

        if (!clientRepository.existsById(clientId)) {
            throw new RuntimeException("Клиент с ID " + clientId + " не найден");
        }

        return cartRepository.findByClientIdAndItemId(clientId, itemId);
    }

    // Вспомогательный метод для проверки существования товара в корзине
    public boolean isItemInCart(Long clientId, Long itemId) {
        // Валидация входных данных
        validateClientId(clientId);
        validateItemId(itemId);

        return cartRepository.findByClientIdAndItemId(clientId, itemId).isPresent();
    }

    // Получение количества конкретного товара в корзине
    public int getItemQuantityInCart(Long clientId, Long itemId) {
        // Валидация входных данных
        validateClientId(clientId);
        validateItemId(itemId);

        Optional<Cart> cartItem = cartRepository.findByClientIdAndItemId(clientId, itemId);
        return cartItem.map(Cart::getQuantity).orElse(0);
    }

    // === МЕТОДЫ ВАЛИДАЦИИ ===

    /**
     * Валидация данных корзины
     */
    private void validateCartData(Long clientId, Long itemId, int quantity) {
        validateClientId(clientId);
        validateItemId(itemId);
        validateQuantity(quantity);
    }

    /**
     * Валидация ID клиента
     */
    private void validateClientId(Long clientId) {
        if (clientId == null || clientId <= 0) {
            throw new RuntimeException("Неверный идентификатор клиента");
        }
    }

    /**
     * Валидация ID товара
     */
    private void validateItemId(Long itemId) {
        if (itemId == null || itemId <= 0) {
            throw new RuntimeException("Неверный идентификатор товара");
        }
    }

    /**
     * Валидация количества
     */
    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("Количество должно быть положительным числом");
        }

        if (quantity > MAX_QUANTITY_PER_ITEM) {
            throw new RuntimeException("Слишком большое количество товара. Максимум: " + MAX_QUANTITY_PER_ITEM);
        }
    }

    /**
     * Валидация максимального количества на один товар
     */
    private void validateMaxQuantityPerItem(int quantity) {
        if (quantity > MAX_QUANTITY_PER_ITEM) {
            throw new RuntimeException("Превышено максимальное количество товара. Максимум: " + MAX_QUANTITY_PER_ITEM);
        }
    }

    /**
     * Валидация общего количества товаров в корзине
     */
    private void validateTotalItemsInCart(Long clientId) {
        Long currentItemsCount = cartRepository.countByClientId(clientId);
        if (currentItemsCount >= MAX_TOTAL_ITEMS_IN_CART) {
            throw new RuntimeException("Корзина переполнена. Максимум " + MAX_TOTAL_ITEMS_IN_CART + " различных товаров");
        }
    }

    /**
     * Проверка возможности добавления товара в корзину
     */
    public boolean canAddToCart(Long clientId, Long itemId, int quantity) {
        try {
            validateCartData(clientId, itemId, quantity);

            if (!clientRepository.existsById(clientId)) {
                return false;
            }

            Optional<Item> itemOpt = itemRepository.findById(itemId);
            if (itemOpt.isEmpty()) {
                return false;
            }

            Item item = itemOpt.get();

            // Проверяем общее количество после добавления
            Optional<Cart> existingCartItem = cartRepository.findByClientIdAndItemId(clientId, itemId);
            if (existingCartItem.isPresent()) {
                int newQuantity = existingCartItem.get().getQuantity() + quantity;
                if (newQuantity > MAX_QUANTITY_PER_ITEM || item.getQuantity() < newQuantity) {
                    return false;
                }
            } else {
                // Проверяем общее количество товаров в корзине
                Long currentItemsCount = cartRepository.countByClientId(clientId);
                if (currentItemsCount >= MAX_TOTAL_ITEMS_IN_CART) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            logger.debug("Товар не может быть добавлен в корзину: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Получение информации о доступности товара для корзины
     */
    public CartAvailabilityInfo getCartAvailabilityInfo(Long clientId, Long itemId, int quantity) {
        CartAvailabilityInfo info = new CartAvailabilityInfo();

        try {
            validateCartData(clientId, itemId, quantity);
            info.setValid(true);

            if (!clientRepository.existsById(clientId)) {
                info.setValid(false);
                info.setMessage("Клиент не найден");
                return info;
            }

            Optional<Item> itemOpt = itemRepository.findById(itemId);
            if (itemOpt.isEmpty()) {
                info.setValid(false);
                info.setMessage("Товар не найден");
                return info;
            }

            Item item = itemOpt.get();
            info.setItemName(item.getName());
            info.setAvailableQuantity(item.getQuantity());

            if (item.getQuantity() < quantity) {
                info.setValid(false);
                info.setMessage("Недостаточно товара на складе");
                return info;
            }

            // Проверяем общее количество после добавления
            Optional<Cart> existingCartItem = cartRepository.findByClientIdAndItemId(clientId, itemId);
            if (existingCartItem.isPresent()) {
                int newQuantity = existingCartItem.get().getQuantity() + quantity;
                info.setCurrentQuantityInCart(existingCartItem.get().getQuantity());
                info.setNewQuantity(newQuantity);

                if (newQuantity > MAX_QUANTITY_PER_ITEM) {
                    info.setValid(false);
                    info.setMessage("Превышено максимальное количество товара");
                    return info;
                }

                if (item.getQuantity() < newQuantity) {
                    info.setValid(false);
                    info.setMessage("Недостаточно товара на складе с учетом корзины");
                    return info;
                }
            } else {
                // Проверяем общее количество товаров в корзине
                Long currentItemsCount = cartRepository.countByClientId(clientId);
                if (currentItemsCount >= MAX_TOTAL_ITEMS_IN_CART) {
                    info.setValid(false);
                    info.setMessage("Корзина переполнена");
                    return info;
                }
            }

            info.setMessage("Товар может быть добавлен в корзину");
            return info;

        } catch (Exception e) {
            info.setValid(false);
            info.setMessage(e.getMessage());
            return info;
        }
    }

    /**
     * Класс для информации о доступности товара в корзине
     */
    public static class CartAvailabilityInfo {
        private boolean valid;
        private String message;
        private String itemName;
        private int availableQuantity;
        private boolean itemAvailable;
        private int currentQuantityInCart;
        private int newQuantity;

        // Геттеры и сеттеры
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }

        public int getAvailableQuantity() { return availableQuantity; }
        public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }

        public boolean isItemAvailable() { return itemAvailable; }
        public void setItemAvailable(boolean itemAvailable) { this.itemAvailable = itemAvailable; }

        public int getCurrentQuantityInCart() { return currentQuantityInCart; }
        public void setCurrentQuantityInCart(int currentQuantityInCart) { this.currentQuantityInCart = currentQuantityInCart; }

        public int getNewQuantity() { return newQuantity; }
        public void setNewQuantity(int newQuantity) { this.newQuantity = newQuantity; }
    }
}