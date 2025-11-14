package com.example.AutoDetail.service;

import com.example.AutoDetail.dto.OrderItemDto;
import com.example.AutoDetail.entity.*;
import com.example.AutoDetail.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ManagerService {

    private static final Logger logger = LoggerFactory.getLogger(ManagerService.class);

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final ClientRepository clientRepository;
    private final OrderStatusRepository statusRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    public ManagerService(OrderRepository orderRepository,
                          ItemRepository itemRepository,
                          ClientRepository clientRepository,
                          OrderStatusRepository statusRepository,
                          UserRepository userRepository,
                          OrderItemRepository orderItemRepository,
                          EmailService emailService,
                          ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.clientRepository = clientRepository;
        this.statusRepository = statusRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

    // === ТОВАРЫ (Только чтение) ===
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public List<Item> searchItemsByName(String name) {
        return itemRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Item> searchItemsByArctical(String arctical) {
        return itemRepository.findByArcticalContainingIgnoreCase(arctical);
    }

    // УЛУЧШЕННЫЙ ПОИСК ТОВАРОВ
    public List<Item> searchItems(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return itemRepository.findAll();
        }
        String cleanSearchTerm = searchTerm.trim();
        return itemRepository.searchItems(cleanSearchTerm);
    }

    public List<Item> searchItemsStartsWith(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }
        return itemRepository.searchItemsStartsWith(searchTerm.trim());
    }

    public List<Item> filterItemsByPrice(Double minPrice, Double maxPrice) {
        return itemRepository.findByPriceRange(minPrice, maxPrice);
    }

    public List<Item> getAvailableItems() {
        return itemRepository.findByQuantityGreaterThan(0);
    }

    // === КЛИЕНТЫ (Только чтение) ===
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public List<Client> searchClients(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return clientRepository.findAll();
        }
        String cleanSearchTerm = searchTerm.trim();
        return clientRepository.searchClients(cleanSearchTerm);
    }

    // УЛУЧШЕННЫЙ ПОИСК КЛИЕНТОВ
    public List<Client> searchClientsExtended(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return clientRepository.findAll();
        }
        String cleanSearchTerm = searchTerm.trim();
        return clientRepository.searchClientsExtended(cleanSearchTerm);
    }

    public List<Client> searchClientsStartsWith(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }
        return clientRepository.searchClientsStartsWith(searchTerm.trim());
    }

    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    // === ЗАКАЗЫ (CRUD операции) ===
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    // УЛУЧШЕННЫЙ ПОИСК ЗАКАЗОВ
    public List<Order> searchOrders(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return orderRepository.findAll();
        }
        String cleanSearchTerm = searchTerm.trim();
        return orderRepository.searchOrders(cleanSearchTerm);
    }

    @Transactional
    public Order saveOrder(Order order) {
        try {
            // Валидация
            if (order.getClientId() == null) {
                throw new RuntimeException("Клиент обязателен");
            }
            if (order.getTotalAmount() == null || order.getTotalAmount() <= 0) {
                throw new RuntimeException("Сумма заказа должна быть положительной");
            }
            if (order.getStatusId() == null) {
                order.setStatusId(1L); // "Оформлен"
            }

            if (order.getCreatedAt() == null) {
                order.setCreatedAt(LocalDateTime.now());
            }

            // Проверяем существование клиента
            if (!clientRepository.existsById(order.getClientId())) {
                throw new RuntimeException("Клиент не найден с ID: " + order.getClientId());
            }

            return orderRepository.save(order);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сохранения заказа: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteOrder(Long id) {
        try {
            Optional<Order> orderOpt = orderRepository.findById(id);
            if (orderOpt.isPresent()) {
                // Сначала удаляем связанные товары заказа
                List<OrderItem> orderItems = orderItemRepository.findByOrderId(id);
                orderItemRepository.deleteAll(orderItems);

                // Затем удаляем сам заказ
                orderRepository.deleteById(id);
                logger.info("✅ Заказ №{} успешно удален", id);
            } else {
                throw new RuntimeException("Заказ не найден с ID: " + id);
            }
        } catch (Exception e) {
            logger.error("❌ Ошибка удаления заказа №{}: {}", id, e.getMessage());
            throw new RuntimeException("Ошибка удаления заказа: " + e.getMessage(), e);
        }
    }

    public List<Order> getOrdersByClientId(Long clientId) {
        return orderRepository.findByClientId(clientId);
    }

    public List<Order> getOrdersByStatus(Long statusId) {
        return orderRepository.findByStatusId(statusId);
    }

    // === РАБОТА С ТОВАРАМИ В ЗАКАЗЕ ===

    /**
     * Создание заказа с товарами
     */
    @Transactional
    public Order createOrderWithItems(Order order, List<OrderItemDto> orderItems, Long managerId) {
        try {
            // Устанавливаем менеджера
            order.setUserId(managerId);

            // Валидация
            if (order.getClientId() == null) {
                throw new RuntimeException("Клиент обязателен");
            }

            if (orderItems == null || orderItems.isEmpty()) {
                throw new RuntimeException("Добавьте хотя бы один товар в заказ");
            }

            // Рассчитываем общую сумму из товаров
            double totalAmount = orderItems.stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();
            order.setTotalAmount(totalAmount);

            // Устанавливаем дату создания
            if (order.getCreatedAt() == null) {
                order.setCreatedAt(LocalDateTime.now());
            }

            // Устанавливаем статус по умолчанию
            if (order.getStatusId() == null) {
                order.setStatusId(1L); // "Оформлен"
            }

            // Сохраняем заказ
            Order savedOrder = orderRepository.save(order);

            // Сохраняем товары заказа
            for (OrderItemDto itemDto : orderItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(savedOrder);

                // Находим товар
                Item item = itemRepository.findById(itemDto.getItemId())
                        .orElseThrow(() -> new RuntimeException("Товар не найден с ID: " + itemDto.getItemId()));

                // Проверяем наличие
                if (item.getQuantity() < itemDto.getQuantity()) {
                    throw new RuntimeException("Недостаточно товара: " + item.getName() + ". В наличии: " + item.getQuantity());
                }

                // Обновляем количество товара
                item.setQuantity(item.getQuantity() - itemDto.getQuantity());
                itemRepository.save(item);

                orderItem.setItem(item);
                orderItem.setQuantity(itemDto.getQuantity());
                orderItem.setPrice(itemDto.getPrice());
                orderItemRepository.save(orderItem);
            }

            // Отправляем email уведомление
            sendOrderCreatedEmail(savedOrder);

            logger.info("✅ Заказ №{} успешно создан менеджером {}. Товаров: {}, Сумма: {}",
                    savedOrder.getId(), managerId, orderItems.size(), totalAmount);

            return savedOrder;
        } catch (Exception e) {
            logger.error("❌ Ошибка создания заказа: {}", e.getMessage());
            throw new RuntimeException("Ошибка создания заказа: " + e.getMessage(), e);
        }
    }

    /**
     * Обновление статуса заказа с отправкой email
     */
    @Transactional
    public Order updateOrderStatus(Long orderId, Long statusId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Заказ не найден"));

            Long oldStatusId = order.getStatusId();
            order.setStatusId(statusId);
            Order updatedOrder = orderRepository.save(order);

            // Отправляем email только если статус изменился
            if (!oldStatusId.equals(statusId)) {
                sendOrderStatusUpdateEmail(updatedOrder);
                logger.info("✅ Статус заказа №{} изменен на {}", orderId, statusId);
            }

            return updatedOrder;
        } catch (Exception e) {
            logger.error("❌ Ошибка обновления статуса заказа №{}: {}", orderId, e.getMessage());
            throw new RuntimeException("Ошибка обновления статуса заказа: " + e.getMessage(), e);
        }
    }

    /**
     * Получить товары заказа
     */
    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    /**
     * Получить ID текущего авторизованного пользователя
     */
    public Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                Optional<User> user = userRepository.findByLogin(username);
                return user.map(User::getId).orElse(1L);
            }
        } catch (Exception e) {
            logger.warn("Не удалось получить ID текущего пользователя: {}", e.getMessage());
        }
        return 1L;
    }

    /**
     * Получить текущего пользователя
     */
    public Optional<User> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                return userRepository.findByLogin(username);
            }
        } catch (Exception e) {
            logger.warn("Не удалось получить текущего пользователя: {}", e.getMessage());
        }
        return Optional.empty();
    }

    // === СТАТУСЫ ЗАКАЗОВ ===
    public List<OrderStatus> getAllStatuses() {
        return statusRepository.findAll();
    }

    public Optional<OrderStatus> getStatusById(Long id) {
        return statusRepository.findById(id);
    }

    // === ПОЛЬЗОВАТЕЛИ ===
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // === МЕТОДЫ ДЛЯ ПОЛУЧЕНИЯ ФИО ===

    /**
     * Получить полное ФИО клиента по его ID
     */
    public String getClientFullName(Long clientId) {
        if (clientId == null) return "Не указан";
        try {
            return clientRepository.findById(clientId)
                    .map(client -> {
                        String fullName = client.getSurname() + " " + client.getName();
                        if (client.getPatronymic() != null && !client.getPatronymic().trim().isEmpty()) {
                            fullName += " " + client.getPatronymic();
                        }
                        return fullName;
                    })
                    .orElse("Клиент не найден");
        } catch (Exception e) {
            return "Ошибка загрузки";
        }
    }

    /**
     * Получить полное ФИО пользователя (менеджера) по его ID
     */
    public String getUserFullName(Long userId) {
        if (userId == null) return "Не назначен";
        try {
            return userRepository.findById(userId)
                    .map(user -> {
                        String fullName = user.getSurname() + " " + user.getName();
                        if (user.getPatronymic() != null && !user.getPatronymic().trim().isEmpty()) {
                            fullName += " " + user.getPatronymic();
                        }
                        return fullName;
                    })
                    .orElse("Менеджер не найден");
        } catch (Exception e) {
            return "Ошибка загрузки";
        }
    }

    /**
     * Получить краткое ФИО клиента (только фамилия и имя)
     */
    public String getClientShortName(Long clientId) {
        if (clientId == null) return "Не указан";
        try {
            return clientRepository.findById(clientId)
                    .map(client -> client.getSurname() + " " + client.getName())
                    .orElse("Клиент не найден");
        } catch (Exception e) {
            return "Ошибка загрузки";
        }
    }

    /**
     * Получить email клиента по ID
     */
    public String getClientEmail(Long clientId) {
        if (clientId == null) return "Не указан";
        try {
            return clientRepository.findById(clientId)
                    .map(Client::getEmail)
                    .orElse("Email не найден");
        } catch (Exception e) {
            return "Ошибка загрузки";
        }
    }

    /**
     * Получить бренд машины клиента
     */
    public String getClientCarBrand(Long clientId) {
        if (clientId == null) return "Не указана";
        try {
            return clientRepository.findById(clientId)
                    .map(client -> {
                        if (client.getCar() != null && client.getCar().getCarBrand() != null) {
                            return client.getCar().getCarBrand();
                        }
                        return "Не указана";
                    })
                    .orElse("Машина не найдена");
        } catch (Exception e) {
            return "Ошибка загрузки";
        }
    }

    // === ПРИВАТНЫЕ МЕТОДЫ ДЛЯ EMAIL УВЕДОМЛЕНИЙ ===

    /**
     * Отправка email о создании заказа
     */
    private void sendOrderCreatedEmail(Order order) {
        try {
            Client client = clientRepository.findById(order.getClientId())
                    .orElseThrow(() -> new RuntimeException("Клиент не найден"));

            if (client.getEmail() == null || client.getEmail().trim().isEmpty()) {
                logger.warn("⚠️ Email клиента {} не указан", client.getId());
                return;
            }

            String clientName = client.getSurname() + " " + client.getName();
            emailService.sendOrderCreatedNotification(client.getEmail(), clientName, order.getId(), order.getTotalAmount());

            logger.info("📧 Email отправлен клиенту {} о создании заказа №{}", clientName, order.getId());
        } catch (Exception e) {
            logger.error("❌ Ошибка отправки email о создании заказа: {}", e.getMessage());
        }
    }

    /**
     * Отправка email об изменении статуса заказа
     */
    private void sendOrderStatusUpdateEmail(Order order) {
        try {
            Client client = clientRepository.findById(order.getClientId())
                    .orElseThrow(() -> new RuntimeException("Клиент не найден"));

            if (client.getEmail() == null || client.getEmail().trim().isEmpty()) {
                logger.warn("⚠️ Email клиента {} не указан", client.getId());
                return;
            }

            OrderStatus status = statusRepository.findById(order.getStatusId())
                    .orElseThrow(() -> new RuntimeException("Статус не найден"));

            String clientName = client.getSurname() + " " + client.getName();
            emailService.sendOrderStatusUpdateNotification(client.getEmail(), clientName, order.getId(), status.getStatus());

            logger.info("📧 Email отправлен клиенту {} об изменении статуса заказа №{} на {}",
                    clientName, order.getId(), status.getStatus());
        } catch (Exception e) {
            logger.error("❌ Ошибка отправки email об изменении статуса заказа: {}", e.getMessage());
        }
    }

    /**
     * Парсинг JSON с товарами заказа
     */
    public List<OrderItemDto> parseOrderItems(String orderItemsJson) {
        try {
            return objectMapper.readValue(
                    orderItemsJson,
                    new TypeReference<List<OrderItemDto>>() {}
            );
        } catch (Exception e) {
            throw new RuntimeException("Ошибка парсинга товаров заказа: " + e.getMessage(), e);
        }
    }
}