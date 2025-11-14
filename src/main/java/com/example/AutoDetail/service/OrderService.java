package com.example.AutoDetail.service;

import com.example.AutoDetail.entity.*;
import com.example.AutoDetail.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final ClientRepository clientRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        OrderStatusRepository orderStatusRepository,
                        CartRepository cartRepository,
                        ItemRepository itemRepository,
                        ClientRepository clientRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.cartRepository = cartRepository;
        this.itemRepository = itemRepository;
        this.clientRepository = clientRepository;
    }

    @Transactional
    public Order createOrderFromCart(Long clientId, String paymentMethod, String cardDetails) {
        List<Cart> cartItems = cartRepository.findByClientId(clientId);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Корзина пуста");
        }

        // Проверяем наличие товаров
        for (Cart cartItem : cartItems) {
            if (cartItem.getItem().getQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Недостаточно товара: " + cartItem.getItem().getName());
            }
        }

        Order order = createOrder(clientId, cartItems);

        // Создаем элементы заказа и обновляем количество
        for (Cart cartItem : cartItems) {
            createOrderItem(order, cartItem);
            updateItemQuantity(cartItem.getItem(), cartItem.getQuantity());
        }

        cartRepository.deleteByClientId(clientId);
        return order;
    }

    @Transactional
    public Order createDirectOrder(Long clientId, Long itemId, int quantity,
                                   String paymentMethod, String cardDetails) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));

        if (item.getQuantity() < quantity) {
            throw new RuntimeException("Недостаточно товара на складе");
        }

        Cart cartItem = new Cart();
        cartItem.setItem(item);
        cartItem.setQuantity(quantity);

        Order order = createOrder(clientId, List.of(cartItem));
        createOrderItem(order, cartItem);
        updateItemQuantity(item, quantity);

        return order;
    }

    private Order createOrder(Long clientId, List<Cart> cartItems) {
        Order order = new Order();
        order.setClientId(clientId);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatusId(1L); // "Оформлен"

        double totalAmount = cartItems.stream()
                .mapToDouble(cart -> cart.getItem().getPrice() * cart.getQuantity())
                .sum();
        order.setTotalAmount(totalAmount);

        return orderRepository.save(order);
    }

    private void createOrderItem(Order order, Cart cartItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setItem(cartItem.getItem());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPrice(cartItem.getItem().getPrice());
        orderItemRepository.save(orderItem);
    }

    private void updateItemQuantity(Item item, int quantity) {
        item.setQuantity(item.getQuantity() - quantity);
        itemRepository.save(item);
    }

    public List<Order> getClientOrders(Long clientId) {
        return orderRepository.findByClientId(clientId);
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public Optional<OrderStatus> getOrderStatus(Long statusId) {
        return orderStatusRepository.findById(statusId);
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Transactional
    public void updateOrderStatus(Long orderId, Long statusId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));
        order.setStatusId(statusId);
        orderRepository.save(order);
    }
}