package com.example.AutoDetail.controller.manager;

import com.example.AutoDetail.entity.*;
import com.example.AutoDetail.service.ManagerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        try {
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
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки dashboard: " + e.getMessage());
            return "error";
        }
    }

    // === ТОВАРЫ (Только чтение) ===
    @GetMapping("/items")
    public String itemsPage(@RequestParam(value = "search", required = false) String search,
                            @RequestParam(value = "arctical", required = false) String arctical,
                            @RequestParam(value = "minPrice", required = false) Double minPrice,
                            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
                            @RequestParam(value = "availableOnly", required = false) Boolean availableOnly,
                            Model model) {
        try {
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
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки товаров: " + e.getMessage());
            return "error";
        }
    }

    // === КЛИЕНТЫ (Только чтение) ===
    @GetMapping("/clients")
    public String clientsPage(@RequestParam(value = "search", required = false) String search,
                              Model model) {
        try {
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
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки клиентов: " + e.getMessage());
            return "error";
        }
    }

    // === ЗАКАЗЫ (CRUD операции) ===
    @GetMapping("/orders")
    public String ordersPage(@RequestParam(value = "clientId", required = false) Long clientId,
                             @RequestParam(value = "statusId", required = false) Long statusId,
                             @RequestParam(value = "search", required = false) String search,
                             Model model) {
        try {
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
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки заказов: " + e.getMessage());
            return "error";
        }
    }

    // Форма создания заказа
    @GetMapping("/orders/create")
    public String createOrderForm(Model model) {
        try {
            List<OrderStatus> statuses = managerService.getAllStatuses();
            List<Client> clients = managerService.getAllClients();

            Order newOrder = new Order();
            newOrder.setCreatedAt(LocalDateTime.now());

            model.addAttribute("order", newOrder);
            model.addAttribute("statuses", statuses);
            model.addAttribute("clients", clients);
            model.addAttribute("isEdit", false);

            return "manager/order-form";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки формы: " + e.getMessage());
            return "error";
        }
    }

    // Создание заказа
    @PostMapping("/orders")
    public String createOrder(@ModelAttribute Order order, RedirectAttributes redirectAttributes, Model model) {
        try {
            // Валидация обязательных полей
            if (order.getClientId() == null) {
                model.addAttribute("error", "Клиент обязателен для заполнения");
                List<OrderStatus> statuses = managerService.getAllStatuses();
                List<Client> clients = managerService.getAllClients();
                model.addAttribute("statuses", statuses);
                model.addAttribute("clients", clients);
                model.addAttribute("order", order);
                model.addAttribute("isEdit", false);
                return "manager/order-form";
            }

            if (order.getTotalAmount() == null || order.getTotalAmount() <= 0) {
                model.addAttribute("error", "Сумма заказа должна быть положительным числом");
                List<OrderStatus> statuses = managerService.getAllStatuses();
                List<Client> clients = managerService.getAllClients();
                model.addAttribute("statuses", statuses);
                model.addAttribute("clients", clients);
                model.addAttribute("order", order);
                model.addAttribute("isEdit", false);
                return "manager/order-form";
            }

            if (order.getStatusId() == null) {
                model.addAttribute("error", "Статус заказа обязателен для заполнения");
                List<OrderStatus> statuses = managerService.getAllStatuses();
                List<Client> clients = managerService.getAllClients();
                model.addAttribute("statuses", statuses);
                model.addAttribute("clients", clients);
                model.addAttribute("order", order);
                model.addAttribute("isEdit", false);
                return "manager/order-form";
            }

            managerService.saveOrder(order);
            redirectAttributes.addFlashAttribute("success", "Заказ успешно создан");
            return "redirect:/manager/orders";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при создании заказа: " + e.getMessage());
            List<OrderStatus> statuses = managerService.getAllStatuses();
            List<Client> clients = managerService.getAllClients();
            model.addAttribute("statuses", statuses);
            model.addAttribute("clients", clients);
            model.addAttribute("order", order);
            model.addAttribute("isEdit", false);
            return "manager/order-form";
        }
    }

    // Форма редактирования заказа
    @GetMapping("/orders/edit/{id}")
    public String editOrderForm(@PathVariable Long id, Model model) {
        try {
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
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки формы: " + e.getMessage());
            return "error";
        }
    }

    // Обновление заказа
    @PostMapping("/orders/update/{id}")
    public String updateOrder(@PathVariable Long id, @ModelAttribute Order order, RedirectAttributes redirectAttributes, Model model) {
        try {
            // Валидация обязательных полей
            if (order.getClientId() == null) {
                model.addAttribute("error", "Клиент обязателен для заполнения");
                List<OrderStatus> statuses = managerService.getAllStatuses();
                List<Client> clients = managerService.getAllClients();
                model.addAttribute("statuses", statuses);
                model.addAttribute("clients", clients);
                model.addAttribute("order", order);
                model.addAttribute("isEdit", true);
                return "manager/order-form";
            }

            if (order.getTotalAmount() == null || order.getTotalAmount() <= 0) {
                model.addAttribute("error", "Сумма заказа должна быть положительным числом");
                List<OrderStatus> statuses = managerService.getAllStatuses();
                List<Client> clients = managerService.getAllClients();
                model.addAttribute("statuses", statuses);
                model.addAttribute("clients", clients);
                model.addAttribute("order", order);
                model.addAttribute("isEdit", true);
                return "manager/order-form";
            }

            if (order.getStatusId() == null) {
                model.addAttribute("error", "Статус заказа обязателен для заполнения");
                List<OrderStatus> statuses = managerService.getAllStatuses();
                List<Client> clients = managerService.getAllClients();
                model.addAttribute("statuses", statuses);
                model.addAttribute("clients", clients);
                model.addAttribute("order", order);
                model.addAttribute("isEdit", true);
                return "manager/order-form";
            }

            order.setId(id);
            managerService.saveOrder(order);
            redirectAttributes.addFlashAttribute("success", "Заказ успешно обновлен");
            return "redirect:/manager/orders";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при обновлении заказа: " + e.getMessage());
            List<OrderStatus> statuses = managerService.getAllStatuses();
            List<Client> clients = managerService.getAllClients();
            model.addAttribute("statuses", statuses);
            model.addAttribute("clients", clients);
            model.addAttribute("order", order);
            model.addAttribute("isEdit", true);
            return "manager/order-form";
        }
    }

    // Удаление заказа
    @PostMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            managerService.deleteOrder(id);
            redirectAttributes.addFlashAttribute("success", "Заказ успешно удален");
            return "redirect:/manager/orders";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении заказа: " + e.getMessage());
            return "redirect:/manager/orders";
        }
    }

    // Просмотр деталей заказа
    @GetMapping("/orders/view/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        try {
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
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки деталей заказа: " + e.getMessage());
            return "error";
        }
    }

    // === СООБЩЕНИЯ ОТ КЛИЕНТОВ ===
    @GetMapping("/messages")
    public String messagesPage(Model model) {
        try {
            List<CustomerMessage> messages = managerService.getAllMessages();
            long unreadCount = managerService.getUnreadMessagesCount();

            model.addAttribute("messages", messages);
            model.addAttribute("unreadCount", unreadCount);
            model.addAttribute("totalMessages", messages.size());

            return "manager/messages";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки сообщений: " + e.getMessage());
            return "error";
        }
    }

    // Пометка сообщения как прочитанного
    @PostMapping("/messages/mark-read/{id}")
    public String markMessageAsRead(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            managerService.markMessageAsRead(id);
            redirectAttributes.addFlashAttribute("success", "Сообщение помечено как прочитанное");
            return "redirect:/manager/messages";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении сообщения: " + e.getMessage());
            return "redirect:/manager/messages";
        }
    }

    // Пометка всех сообщений как прочитанных
    @PostMapping("/messages/mark-all-read")
    public String markAllMessagesAsRead(RedirectAttributes redirectAttributes) {
        try {
            List<CustomerMessage> unreadMessages = managerService.getUnreadMessages();
            for (CustomerMessage message : unreadMessages) {
                message.setIsRead(true);
                managerService.saveMessage(message);
            }
            redirectAttributes.addFlashAttribute("success", "Все сообщения помечены как прочитанные");
            return "redirect:/manager/messages";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении сообщений: " + e.getMessage());
            return "redirect:/manager/messages";
        }
    }
}