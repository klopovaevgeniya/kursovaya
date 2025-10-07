package com.example.AutoDetail.service;

import com.example.AutoDetail.entity.*;
import com.example.AutoDetail.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ManagerService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final ClientRepository clientRepository;
    private final CustomerMessageRepository messageRepository;
    private final OrderStatusRepository statusRepository;

    public ManagerService(OrderRepository orderRepository,
                          ItemRepository itemRepository,
                          ClientRepository clientRepository,
                          CustomerMessageRepository messageRepository,
                          OrderStatusRepository statusRepository) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.clientRepository = clientRepository;
        this.messageRepository = messageRepository;
        this.statusRepository = statusRepository;
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
        return clientRepository.searchClients(searchTerm);
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

    public Order saveOrder(Order order) {
        if (order.getCreatedAt() == null) {
            order.setCreatedAt(LocalDateTime.now());
        }
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    public List<Order> getOrdersByClientId(Long clientId) {
        return orderRepository.findByClientId(clientId);
    }

    public List<Order> getOrdersByStatus(Long statusId) {
        return orderRepository.findByStatusId(statusId);
    }

    // === СООБЩЕНИЯ ===
    public List<CustomerMessage> getAllMessages() {
        return messageRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<CustomerMessage> getUnreadMessages() {
        return messageRepository.findByIsReadFalse();
    }

    public CustomerMessage saveMessage(CustomerMessage message) {
        if (message.getCreatedAt() == null) {
            message.setCreatedAt(LocalDateTime.now());
        }
        if (message.getIsRead() == null) {
            message.setIsRead(false);
        }
        return messageRepository.save(message);
    }

    public void markMessageAsRead(Long messageId) {
        Optional<CustomerMessage> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isPresent()) {
            CustomerMessage message = messageOpt.get();
            message.setIsRead(true);
            messageRepository.save(message);
        }
    }

    public long getUnreadMessagesCount() {
        return messageRepository.findByIsReadFalse().size();
    }

    // === СТАТУСЫ ЗАКАЗОВ ===
    public List<OrderStatus> getAllStatuses() {
        return statusRepository.findAll();
    }

    public Optional<OrderStatus> getStatusById(Long id) {
        return statusRepository.findById(id);
    }
}