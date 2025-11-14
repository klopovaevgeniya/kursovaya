package com.example.AutoDetail.controller.manager;

import com.example.AutoDetail.dto.OrderItemDto;
import com.example.AutoDetail.entity.*;
import com.example.AutoDetail.service.ManagerService;
import com.example.AutoDetail.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final ManagerService managerService;
    private final EmailService emailService;

    public ManagerController(ManagerService managerService, EmailService emailService) {
        this.managerService = managerService;
        this.emailService = emailService;
    }

    // === ГЛАВНАЯ ПАНЕЛЬ ===
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            List<Order> allOrders = managerService.getAllOrders();

            List<Order> recentOrders = allOrders.size() > 5 ? allOrders.subList(0, 5) : allOrders;

            model.addAttribute("title", "Панель менеджера");
            model.addAttribute("recentOrders", recentOrders);
            model.addAttribute("totalOrders", allOrders.size());
            model.addAttribute("totalClients", managerService.getAllClients().size());
            model.addAttribute("managerService", managerService);
            model.addAttribute("currentManager", managerService.getCurrentUser().orElse(null));

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
            String searchType = "все товары";

            if (search != null && !search.isEmpty()) {
                items = managerService.searchItems(search);
                searchType = "Результаты поиска: " + search;
            } else if (arctical != null && !arctical.isEmpty()) {
                items = managerService.searchItemsByArctical(arctical);
                searchType = "По артикулу: " + arctical;
            } else if (minPrice != null && maxPrice != null) {
                items = managerService.filterItemsByPrice(minPrice, maxPrice);
                searchType = "По цене: от " + minPrice + " до " + maxPrice;
            } else if (availableOnly != null && availableOnly) {
                items = managerService.getAvailableItems();
                searchType = "Только в наличии";
            } else {
                items = managerService.getAllItems();
            }

            model.addAttribute("items", items);
            model.addAttribute("search", search);
            model.addAttribute("arctical", arctical);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);
            model.addAttribute("availableOnly", availableOnly);
            model.addAttribute("searchType", searchType);
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
            String searchType = "Все клиенты";

            if (search != null && !search.isEmpty()) {
                clients = managerService.searchClients(search);
                searchType = "Результаты поиска: " + search;
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

    // === ПРОСМОТР ДЕТАЛЕЙ КЛИЕНТА ===
    @GetMapping("/clients/{id}")
    public String viewClientDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Client> clientOpt = managerService.getClientById(id);
            if (clientOpt.isPresent()) {
                Client client = clientOpt.get();
                model.addAttribute("client", client);
                model.addAttribute("title", "Детали клиента - " + client.getName() + " " + client.getSurname());
                return "manager/client-details";
            }
            redirectAttributes.addFlashAttribute("error", "Клиент не найден");
            return "redirect:/manager/clients";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка загрузки деталей клиента: " + e.getMessage());
            return "redirect:/manager/clients";
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
            String filterInfo = "Все заказы";

            if (clientId != null) {
                Optional<Client> client = managerService.getClientById(clientId);
                orders = managerService.getOrdersByClientId(clientId);
                if (client.isPresent()) {
                    filterInfo = "Заказы клиента: " + managerService.getClientShortName(clientId);
                }
            } else if (statusId != null) {
                Optional<OrderStatus> status = managerService.getStatusById(statusId);
                orders = managerService.getOrdersByStatus(statusId);
                if (status.isPresent()) {
                    filterInfo = "Заказы со статусом: " + status.get().getStatus();
                }
            } else {
                orders = managerService.getAllOrders();
            }

            // Улучшенный поиск по ID заказа и другим полям
            if (search != null && !search.isEmpty()) {
                try {
                    Long orderId = Long.parseLong(search);
                    Optional<Order> foundOrder = managerService.getOrderById(orderId);
                    if (foundOrder.isPresent()) {
                        orders = List.of(foundOrder.get());
                        filterInfo = "Заказ №" + search;
                    } else {
                        // Если не нашли по ID, ищем по другим полям
                        orders = managerService.searchOrders(search);
                        filterInfo = "Результаты поиска: " + search;
                    }
                } catch (NumberFormatException e) {
                    // Поиск по другим полям если не число
                    orders = managerService.searchOrders(search);
                    filterInfo = "Результаты поиска: " + search;
                }
            }

            List<OrderStatus> statuses = managerService.getAllStatuses();
            List<Client> clients = managerService.getAllClients();

            model.addAttribute("managerService", managerService);
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

    // Форма создания заказа с товарами
    @GetMapping("/orders/create")
    public String createOrderForm(Model model) {
        try {
            List<OrderStatus> statuses = managerService.getAllStatuses();
            List<Client> clients = managerService.getAllClients();
            List<Item> availableItems = managerService.getAvailableItems();

            Order newOrder = new Order();
            newOrder.setCreatedAt(LocalDateTime.now());

            // Автоматически подставляем текущего менеджера
            Long currentManagerId = managerService.getCurrentUserId();
            newOrder.setUserId(currentManagerId);

            model.addAttribute("order", newOrder);
            model.addAttribute("statuses", statuses);
            model.addAttribute("clients", clients);
            model.addAttribute("availableItems", availableItems);
            model.addAttribute("isEdit", false);
            model.addAttribute("managerService", managerService);
            model.addAttribute("currentManagerId", currentManagerId);
            model.addAttribute("currentManager", managerService.getCurrentUser().orElse(null));

            return "manager/order-form";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки формы: " + e.getMessage());
            return "error";
        }
    }

    // Создание заказа с товарами
    @PostMapping("/orders/create-with-items")
    public String createOrderWithItems(@Valid @ModelAttribute Order order,
                                       BindingResult bindingResult,
                                       @RequestParam("statusId") Long statusId,
                                       @RequestParam("orderItems") String orderItemsJson,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                List<OrderStatus> statuses = managerService.getAllStatuses();
                List<Client> clients = managerService.getAllClients();
                List<Item> availableItems = managerService.getAvailableItems();

                model.addAttribute("statuses", statuses);
                model.addAttribute("clients", clients);
                model.addAttribute("availableItems", availableItems);
                model.addAttribute("isEdit", false);
                model.addAttribute("managerService", managerService);
                model.addAttribute("currentManagerId", managerService.getCurrentUserId());
                model.addAttribute("currentManager", managerService.getCurrentUser().orElse(null));

                return "manager/order-form";
            }

            order.setStatusId(statusId);

            // Парсим товары заказа
            List<OrderItemDto> orderItems = managerService.parseOrderItems(orderItemsJson);

            // Получаем ID текущего менеджера
            Long currentManagerId = managerService.getCurrentUserId();

            // Создаем заказ с товарами
            Order createdOrder = managerService.createOrderWithItems(order, orderItems, currentManagerId);

            redirectAttributes.addFlashAttribute("success",
                    "Заказ №" + createdOrder.getId() + " успешно создан. Сумма: " + createdOrder.getTotalAmount() + " ₽");
            return "redirect:/manager/orders";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка создания заказа: " + e.getMessage());
            return "redirect:/manager/orders/create";
        }
    }

    // Старый метод для обратной совместимости
    @PostMapping("/orders")
    public String createOrder(@Valid @ModelAttribute Order order,
                              BindingResult bindingResult,
                              @RequestParam("statusId") Long statusId,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                List<OrderStatus> statuses = managerService.getAllStatuses();
                List<Client> clients = managerService.getAllClients();

                model.addAttribute("statuses", statuses);
                model.addAttribute("clients", clients);
                model.addAttribute("isEdit", false);
                model.addAttribute("managerService", managerService);
                return "manager/order-form";
            }

            order.setStatusId(statusId);

            // Устанавливаем текущего менеджера, если не указан
            if (order.getUserId() == null) {
                order.setUserId(managerService.getCurrentUserId());
            }

            managerService.saveOrder(order);
            redirectAttributes.addFlashAttribute("success", "Заказ успешно создан");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/manager/orders";
    }

    // Форма редактирования заказа
    @GetMapping("/orders/edit/{id}")
    public String editOrderForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Order> orderOpt = managerService.getOrderById(id);
            if (orderOpt.isPresent()) {
                List<OrderStatus> statuses = managerService.getAllStatuses();
                List<Client> clients = managerService.getAllClients();
                List<OrderItem> orderItems = managerService.getOrderItems(id);

                model.addAttribute("order", orderOpt.get());
                model.addAttribute("statuses", statuses);
                model.addAttribute("clients", clients);
                model.addAttribute("orderItems", orderItems);
                model.addAttribute("isEdit", true);
                model.addAttribute("managerService", managerService);

                return "manager/order-form";
            }
            redirectAttributes.addFlashAttribute("error", "Заказ не найден");
            return "redirect:/manager/orders";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка загрузки формы: " + e.getMessage());
            return "redirect:/manager/orders";
        }
    }

    // Обновление заказа
    @PostMapping("/orders/update/{id}")
    public String updateOrder(@PathVariable Long id,
                              @Valid @ModelAttribute Order order,
                              BindingResult bindingResult,
                              @RequestParam("statusId") Long statusId,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                List<OrderStatus> statuses = managerService.getAllStatuses();
                List<Client> clients = managerService.getAllClients();
                List<OrderItem> orderItems = managerService.getOrderItems(id);

                model.addAttribute("statuses", statuses);
                model.addAttribute("clients", clients);
                model.addAttribute("orderItems", orderItems);
                model.addAttribute("isEdit", true);
                model.addAttribute("managerService", managerService);

                return "manager/order-form";
            }

            order.setId(id);

            // Получаем старый заказ для проверки изменения статуса
            Optional<Order> oldOrderOpt = managerService.getOrderById(id);

            order.setStatusId(statusId);
            Order updatedOrder = managerService.saveOrder(order);

            // Если статус изменился - отправляем email
            if (oldOrderOpt.isPresent() && !oldOrderOpt.get().getStatusId().equals(statusId)) {
                managerService.updateOrderStatus(id, statusId);
            }

            redirectAttributes.addFlashAttribute("success", "Заказ успешно обновлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/manager/orders";
    }

    // Удаление заказа
    @PostMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            managerService.deleteOrder(id);
            redirectAttributes.addFlashAttribute("success", "Заказ успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/manager/orders";
    }

    // Просмотр деталей заказа
    @GetMapping("/orders/view/{id}")
    public String viewOrder(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Order> orderOpt = managerService.getOrderById(id);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                Optional<Client> clientOpt = managerService.getClientById(order.getClientId());
                Optional<OrderStatus> statusOpt = managerService.getStatusById(order.getStatusId());
                List<OrderItem> orderItems = managerService.getOrderItems(id);

                model.addAttribute("order", order);
                model.addAttribute("client", clientOpt.orElse(null));
                model.addAttribute("status", statusOpt.orElse(null));
                model.addAttribute("orderItems", orderItems);
                model.addAttribute("managerService", managerService);

                return "manager/order-view";
            }
            redirectAttributes.addFlashAttribute("error", "Заказ не найден");
            return "redirect:/manager/orders";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка загрузки деталей заказа: " + e.getMessage());
            return "redirect:/manager/orders";
        }
    }

    /**
     * Быстрое обновление статуса заказа
     */
    @PostMapping("/orders/update-status/{id}")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam("statusId") Long statusId,
                                    RedirectAttributes redirectAttributes) {
        try {
            managerService.updateOrderStatus(id, statusId);
            redirectAttributes.addFlashAttribute("success", "Статус заказа успешно обновлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка обновления статуса: " + e.getMessage());
        }
        return "redirect:/manager/orders";
    }

    // API для autocomplete поиска
    @GetMapping("/api/items/search")
    @ResponseBody
    public List<Item> searchItemsApi(@RequestParam String q) {
        return managerService.searchItemsStartsWith(q);
    }

    @GetMapping("/api/clients/search")
    @ResponseBody
    public List<Client> searchClientsApi(@RequestParam String q) {
        return managerService.searchClientsStartsWith(q);
    }

    @GetMapping("/api/orders/search")
    @ResponseBody
    public List<Order> searchOrdersApi(@RequestParam String q) {
        return managerService.searchOrders(q);
    }
}