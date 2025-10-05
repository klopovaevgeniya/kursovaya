package com.example.AutoDetail.controller.manager;

import com.example.AutoDetail.entity.*;
import com.example.AutoDetail.service.ManagerService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final ManagerService managerService;

    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    // === ГЛАВНАЯ ПАНЕЛЬ ===
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long unreadMessages = managerService.getUnreadMessagesCount();
        List<Order> allOrders = managerService.getAllOrders();
        List<CustomerMessage> recentMessages = managerService.getAllMessages();

        // Берем только последние 5 заказов
        List<Order> recentOrders = allOrders.size() > 5 ?
                allOrders.subList(0, 5) : allOrders;

        // Берем только последние 3 сообщения
        List<CustomerMessage> latestMessages = recentMessages.size() > 3 ?
                recentMessages.subList(0, 3) : recentMessages;

        model.addAttribute("title", "Панель менеджера");
        model.addAttribute("unreadMessages", unreadMessages);
        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("latestMessages", latestMessages);
        model.addAttribute("totalOrders", allOrders.size());
        model.addAttribute("totalClients", managerService.getAllClients().size());

        return "manager/dashboard";
    }

    // === ТОВАРЫ (Только чтение) ===
    @GetMapping("/items")
    public String itemsPage(@RequestParam(value = "search", required = false) String search,
                            @RequestParam(value = "arctical", required = false) String arctical,
                            @RequestParam(value = "minPrice", required = false) Double minPrice,
                            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
                            @RequestParam(value = "availableOnly", required = false) Boolean availableOnly,
                            Model model) {
        List<Item> items;

        if (search != null && !search.isEmpty()) {
            items = managerService.searchItemsByName(search);
            model.addAttribute("searchType", "по названию: " + search);
        } else if (arctical != null && !arctical.isEmpty()) {
            items = managerService.searchItemsByArctical(arctical);
            model.addAttribute("searchType", "по артикулу: " + arctical);
        } else if (minPrice != null && maxPrice != null) {
            items = managerService.filterItemsByPrice(minPrice, maxPrice);
            model.addAttribute("searchType", "по цене: от " + minPrice + " до " + maxPrice);
        } else if (availableOnly != null && availableOnly) {
            items = managerService.getAvailableItems();
            model.addAttribute("searchType", "только в наличии");
        } else {
            items = managerService.getAllItems();
            model.addAttribute("searchType", "все товары");
        }

        model.addAttribute("items", items);
        model.addAttribute("search", search);
        model.addAttribute("arctical", arctical);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("availableOnly", availableOnly);
        model.addAttribute("totalItems", items.size());

        return "manager/items";
    }

    // === КЛИЕНТЫ (Только чтение) ===
    @GetMapping("/clients")
    public String clientsPage(@RequestParam(value = "search", required = false) String search,
                              Model model) {
        List<Client> clients;
        String searchType = "все клиенты";

        if (search != null && !search.isEmpty()) {
            clients = managerService.searchClients(search);
            searchType = "результаты поиска: " + search;
        } else {
            clients = managerService.getAllClients();
        }

        model.addAttribute("clients", clients);
        model.addAttribute("search", search);
        model.addAttribute("searchType", searchType);
        model.addAttribute("totalClients", clients.size());

        return "manager/clients";
    }

    // === ЗАКАЗЫ (CRUD операции) ===
    @GetMapping("/orders")
    public String ordersPage(@RequestParam(value = "clientId", required = false) Long clientId,
                             @RequestParam(value = "statusId", required = false) Long statusId,
                             @RequestParam(value = "search", required = false) String search,
                             Model model) {
        List<Order> orders;
        String filterInfo = "все заказы";

        if (clientId != null) {
            Optional<Client> client = managerService.getClientById(clientId);
            orders = managerService.getOrdersByClientId(clientId);
            if (client.isPresent()) {
                filterInfo = "заказы клиента: " + client.get().getName() + " " + client.get().getSurname();
            }
        } else if (statusId != null) {
            Optional<OrderStatus> status = managerService.getStatusById(statusId);
            orders = managerService.getOrdersByStatus(statusId);
            if (status.isPresent()) {
                filterInfo = "заказы со статусом: " + status.get().getStatus();
            }
        } else {
            orders = managerService.getAllOrders();
        }

        // Поиск по ID заказа
        if (search != null && !search.isEmpty()) {
            try {
                Long orderId = Long.parseLong(search);
                Optional<Order> foundOrder = managerService.getOrderById(orderId);
                if (foundOrder.isPresent()) {
                    orders = List.of(foundOrder.get());
                    filterInfo = "заказ №" + search;
                } else {
                    orders = List.of();
                    filterInfo = "заказ №" + search + " не найден";
                }
            } catch (NumberFormatException e) {
                orders = List.of();
                filterInfo = "неверный номер заказа";
            }
        }

        List<OrderStatus> statuses = managerService.getAllStatuses();
        List<Client> clients = managerService.getAllClients();

        model.addAttribute("orders", orders);
        model.addAttribute("statuses", statuses);
        model.addAttribute("clients", clients);
        model.addAttribute("selectedClientId", clientId);
        model.addAttribute("selectedStatusId", statusId);
        model.addAttribute("search", search);
        model.addAttribute("filterInfo", filterInfo);
        model.addAttribute("totalOrders", orders.size());

        return "manager/orders";
    }

    // Форма создания заказа
    @GetMapping("/orders/create")
    public String createOrderForm(Model model) {
        List<OrderStatus> statuses = managerService.getAllStatuses();
        List<Client> clients = managerService.getAllClients();

        Order newOrder = new Order();
        newOrder.setCreatedAt(LocalDateTime.now());

        model.addAttribute("order", newOrder);
        model.addAttribute("statuses", statuses);
        model.addAttribute("clients", clients);
        model.addAttribute("isEdit", false);

        return "manager/order-form";
    }

    // Создание заказа
    @PostMapping("/orders")
    public String createOrder(@ModelAttribute Order order) {
        managerService.saveOrder(order);
        return "redirect:/manager/orders?success=created";
    }

    // Форма редактирования заказа
    @GetMapping("/orders/edit/{id}")
    public String editOrderForm(@PathVariable Long id, Model model) {
        Optional<Order> orderOpt = managerService.getOrderById(id);
        if (orderOpt.isPresent()) {
            List<OrderStatus> statuses = managerService.getAllStatuses();
            List<Client> clients = managerService.getAllClients();

            model.addAttribute("order", orderOpt.get());
            model.addAttribute("statuses", statuses);
            model.addAttribute("clients", clients);
            model.addAttribute("isEdit", true);

            return "manager/order-form";
        }
        return "redirect:/manager/orders?error=not_found";
    }

    // Обновление заказа
    @PostMapping("/orders/update/{id}")
    public String updateOrder(@PathVariable Long id, @ModelAttribute Order order) {
        order.setId(id);
        managerService.saveOrder(order);
        return "redirect:/manager/orders?success=updated";
    }

    // Удаление заказа
    @PostMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable Long id) {
        managerService.deleteOrder(id);
        return "redirect:/manager/orders?success=deleted";
    }

    // Просмотр деталей заказа
    @GetMapping("/orders/view/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        Optional<Order> orderOpt = managerService.getOrderById(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            Optional<Client> clientOpt = managerService.getClientById(order.getClientId());
            Optional<OrderStatus> statusOpt = managerService.getStatusById(order.getStatusId());

            model.addAttribute("order", order);
            model.addAttribute("client", clientOpt.orElse(null));
            model.addAttribute("status", statusOpt.orElse(null));

            return "manager/order-details";
        }
        return "redirect:/manager/orders?error=not_found";
    }

    // === СООБЩЕНИЯ ОТ КЛИЕНТОВ ===
    @GetMapping("/messages")
    public String messagesPage(Model model) {
        List<CustomerMessage> messages = managerService.getAllMessages();
        long unreadCount = managerService.getUnreadMessagesCount();

        model.addAttribute("messages", messages);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("totalMessages", messages.size());

        return "manager/messages";
    }

    // Пометка сообщения как прочитанного
    @PostMapping("/messages/mark-read/{id}")
    public String markMessageAsRead(@PathVariable Long id) {
        managerService.markMessageAsRead(id);
        return "redirect:/manager/messages";
    }

    // Пометка всех сообщений как прочитанных
    @PostMapping("/messages/mark-all-read")
    public String markAllMessagesAsRead() {
        List<CustomerMessage> unreadMessages = managerService.getUnreadMessages();
        for (CustomerMessage message : unreadMessages) {
            message.setIsRead(true);
            managerService.saveMessage(message);
        }
        return "redirect:/manager/messages?success=all_read";
    }

    // Создание тестового сообщения (для демонстрации)
    @PostMapping("/messages/create-test")
    public String createTestMessage() {
        CustomerMessage testMessage = new CustomerMessage();
        testMessage.setClientId(1L);
        testMessage.setClientName("Иван Иванов");
        testMessage.setMessage("Здравствуйте! Хочу уточнить наличие тормозных колодок для BMW X5");
        testMessage.setCreatedAt(LocalDateTime.now());
        testMessage.setIsRead(false);

        managerService.saveMessage(testMessage);
        return "redirect:/manager/messages?success=test_created";
    }
}