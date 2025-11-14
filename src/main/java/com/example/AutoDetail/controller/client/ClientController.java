package com.example.AutoDetail.controller.client;

import com.example.AutoDetail.entity.*;
import com.example.AutoDetail.repository.*;
import com.example.AutoDetail.service.CartService;
import com.example.AutoDetail.service.ClientProfileService;
import com.example.AutoDetail.service.ItemService;
import com.example.AutoDetail.service.OrderService;
import com.example.AutoDetail.service.EmailService;
import com.example.AutoDetail.service.UserService;
import com.example.AutoDetail.dto.ClientProfileDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Comparator;

@Controller
@RequestMapping("/client")
public class ClientController {

    private final ItemService itemService;
    private final CartService cartService;
    private final OrderService orderService;
    private final CategoryRepository categoryRepository;
    private final ClientRepository clientRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final ClientProfileService clientProfileService;
    private final EmailService emailService;
    private final UserService userService;

    public ClientController(ItemService itemService, CartService cartService,
                            OrderService orderService, CategoryRepository categoryRepository,
                            ClientRepository clientRepository, OrderStatusRepository orderStatusRepository,
                            ClientProfileService clientProfileService, EmailService emailService,
                            UserService userService) {
        this.itemService = itemService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.categoryRepository = categoryRepository;
        this.clientRepository = clientRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.clientProfileService = clientProfileService;
        this.emailService = emailService;
        this.userService = userService;
    }

    // Получение ID текущего клиента из аутентификации
    private Long getCurrentClientId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Optional<Client> client = clientRepository.findByLogin(username);
            if (client.isPresent()) {
                return client.get().getId();
            } else {
                throw new RuntimeException("Клиент не найден для пользователя: " + username);
            }
        }
        throw new RuntimeException("Пользователь не аутентифицирован");
    }

    // Получение логина текущего клиента
    private String getCurrentClientLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "";
    }

    // Получение текущего клиента
    private Client getCurrentClient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Optional<Client> client = clientRepository.findByLogin(username);
            if (client.isPresent()) {
                return client.get();
            } else {
                throw new RuntimeException("Клиент не найден для пользователя: " + username);
            }
        }
        throw new RuntimeException("Пользователь не аутентифицирован");
    }

    // Каталог с поиском, фильтрацией и сортировкой
    @GetMapping("/catalog")
    public String catalog(@RequestParam(required = false) String search,
                          @RequestParam(required = false) Long category,
                          @RequestParam(required = false) String sort,
                          Model model) {

        try {
            Long clientId = getCurrentClientId();
            Client currentClient = getCurrentClient();
            List<Item> items = itemService.getFilteredAndSortedItems(search, category, sort);

            model.addAttribute("items", items);
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("search", search);
            model.addAttribute("selectedCategory", category);
            model.addAttribute("selectedSort", sort);
            model.addAttribute("cartItemsCount", cartService.countItemsInCart(clientId));
            model.addAttribute("currentClientLogin", currentClient.getLogin());

            return "client/catalog";
        } catch (RuntimeException e) {
            // Если клиент не найден, перенаправляем на страницу логина
            return "redirect:/auth/login";
        }
    }

    // Страница связи с менеджерами
    @GetMapping("/contact-managers")
    public String contactManagers(@RequestParam(required = false) String search,
                                  @RequestParam(required = false) String sort,
                                  Model model) {
        try {
            Long clientId = getCurrentClientId();
            Client currentClient = getCurrentClient();

            // Получаем всех менеджеров
            List<User> managers = userService.getUsersByRole(Role.ROLE_MANAGER);

            // Применяем поиск если есть
            if (search != null && !search.trim().isEmpty()) {
                String searchLower = search.toLowerCase().trim();
                managers = managers.stream()
                        .filter(manager ->
                                (manager.getName() != null && manager.getName().toLowerCase().contains(searchLower)) ||
                                        (manager.getSurname() != null && manager.getSurname().toLowerCase().contains(searchLower)) ||
                                        (manager.getPatronymic() != null && manager.getPatronymic().toLowerCase().contains(searchLower)) ||
                                        (manager.getDescription() != null && manager.getDescription().toLowerCase().contains(searchLower)) ||
                                        (manager.getPhone() != null && manager.getPhone().contains(searchLower))
                        )
                        .collect(Collectors.toList());
            }

            // Применяем сортировку
            if (sort != null && !sort.isEmpty()) {
                switch (sort) {
                    case "nameAsc":
                        managers.sort(Comparator.comparing(User::getName,
                                Comparator.nullsLast(String::compareToIgnoreCase)));
                        break;
                    case "nameDesc":
                        managers.sort(Comparator.comparing(User::getName,
                                Comparator.nullsLast(String::compareToIgnoreCase)).reversed());
                        break;
                    case "surnameAsc":
                        managers.sort(Comparator.comparing(User::getSurname,
                                Comparator.nullsLast(String::compareToIgnoreCase)));
                        break;
                    case "surnameDesc":
                        managers.sort(Comparator.comparing(User::getSurname,
                                Comparator.nullsLast(String::compareToIgnoreCase)).reversed());
                        break;
                }
            }

            model.addAttribute("managers", managers);
            model.addAttribute("search", search);
            model.addAttribute("selectedSort", sort);
            model.addAttribute("cartItemsCount", cartService.countItemsInCart(clientId));
            model.addAttribute("currentClientLogin", currentClient.getLogin());

            return "client/contact-managers";
        } catch (RuntimeException e) {
            return "redirect:/auth/login";
        }
    }

    // Детальная страница товара
    @GetMapping("/item/{id}")
    public String itemDetails(@PathVariable Long id, Model model) {
        try {
            Long clientId = getCurrentClientId();
            Client currentClient = getCurrentClient();
            Item item = itemService.getItemById(id);
            if (item == null) {
                return "redirect:/client/catalog";
            }

            List<Item> relatedItems = itemService.getRelatedItems(item);

            model.addAttribute("item", item);
            model.addAttribute("relatedItems", relatedItems);
            model.addAttribute("cartItemsCount", cartService.countItemsInCart(clientId));
            model.addAttribute("currentClientLogin", currentClient.getLogin());

            return "client/item-details";
        } catch (RuntimeException e) {
            return "redirect:/auth/login";
        }
    }

    // Просмотр корзины
    @GetMapping("/cart")
    public String viewCart(Model model) {
        try {
            Long clientId = getCurrentClientId();
            Client currentClient = getCurrentClient();
            ClientController.CartSummary cartSummary = cartService.getCartSummary(clientId);

            model.addAttribute("cartItems", cartSummary.getCartItems());
            model.addAttribute("totalSum", cartSummary.getTotalSum());
            model.addAttribute("totalItems", cartSummary.getTotalItems());
            model.addAttribute("cartItemsCount", cartSummary.getCartItemsCount());
            model.addAttribute("currentClientLogin", currentClient.getLogin());

            return "client/cart";
        } catch (RuntimeException e) {
            return "redirect:/auth/login";
        }
    }

    // Страница оформления заказа из корзины
    @GetMapping("/order/checkout")
    public String checkout(Model model) {
        try {
            Long clientId = getCurrentClientId();
            Client currentClient = getCurrentClient();
            ClientController.CartSummary cartSummary = cartService.getCartSummary(clientId);

            if (cartSummary.getCartItems().isEmpty()) {
                return "redirect:/client/cart";
            }

            model.addAttribute("cartItems", cartSummary.getCartItems());
            model.addAttribute("totalSum", cartSummary.getTotalSum());
            model.addAttribute("cartItemsCount", cartSummary.getCartItemsCount());
            model.addAttribute("currentClientLogin", currentClient.getLogin());

            return "client/checkout";
        } catch (RuntimeException e) {
            return "redirect:/auth/login";
        }
    }

    // Страница оформления заказа с детальной страницы
    @GetMapping("/order/checkout-direct")
    public String checkoutDirect(@RequestParam Long itemId,
                                 @RequestParam int quantity,
                                 Model model) {
        try {
            Long clientId = getCurrentClientId();
            Client currentClient = getCurrentClient();
            Item item = itemService.getItemById(itemId);
            if (item == null) {
                return "redirect:/client/catalog";
            }

            double totalSum = item.getPrice() * quantity;

            model.addAttribute("item", item);
            model.addAttribute("quantity", quantity);
            model.addAttribute("totalSum", totalSum);
            model.addAttribute("cartItemsCount", cartService.countItemsInCart(clientId));
            model.addAttribute("currentClientLogin", currentClient.getLogin());

            return "client/checkout-direct";
        } catch (RuntimeException e) {
            return "redirect:/auth/login";
        }
    }

    // Мои заказы
    @GetMapping("/orders")
    public String orders(Model model) {
        try {
            Long clientId = getCurrentClientId();
            Client currentClient = getCurrentClient();
            List<Order> orders = orderService.getClientOrders(clientId);

            List<OrderDetails> orderDetails = orders.stream()
                    .map(order -> {
                        List<OrderItem> items = orderService.getOrderItems(order.getId());
                        OrderStatus status = orderService.getOrderStatus(order.getStatusId())
                                .orElse(new OrderStatus());
                        return new OrderDetails(order, items, status);
                    })
                    .collect(Collectors.toList());

            model.addAttribute("orders", orderDetails);
            model.addAttribute("cartItemsCount", cartService.countItemsInCart(clientId));
            model.addAttribute("currentClientLogin", currentClient.getLogin());

            return "client/orders";
        } catch (RuntimeException e) {
            return "redirect:/auth/login";
        }
    }

    // Детали заказа
    @GetMapping("/order/{id}")
    public String orderDetails(@PathVariable Long id, Model model) {
        try {
            Long clientId = getCurrentClientId();
            Client currentClient = getCurrentClient();
            Order order = orderService.getOrderById(id)
                    .orElse(null);

            if (order == null || !order.getClientId().equals(clientId)) {
                return "redirect:/client/orders";
            }

            List<OrderItem> orderItems = orderService.getOrderItems(id);
            OrderStatus orderStatus = orderService.getOrderStatus(order.getStatusId())
                    .orElse(new OrderStatus());

            model.addAttribute("order", order);
            model.addAttribute("orderItems", orderItems);
            model.addAttribute("orderStatus", orderStatus);
            model.addAttribute("cartItemsCount", cartService.countItemsInCart(clientId));
            model.addAttribute("currentClientLogin", currentClient.getLogin());

            return "client/order-details";
        } catch (RuntimeException e) {
            return "redirect:/auth/login";
        }
    }

    // === ПРОФИЛЬ КЛИЕНТA ===

    // Страница профиля
    @GetMapping("/profile")
    public String profile(Model model) {
        try {
            String clientLogin = getCurrentClientLogin();
            Client currentClient = getCurrentClient();
            ClientProfileDTO clientProfile = clientProfileService.getClientProfileDTOByLogin(clientLogin);
            Long clientId = getCurrentClientId();

            model.addAttribute("user", clientProfile);
            model.addAttribute("clientProfile", clientProfile);
            model.addAttribute("cartItemsCount", cartService.countItemsInCart(clientId));
            model.addAttribute("currentClientLogin", currentClient.getLogin());

            return "client/profile";
        } catch (RuntimeException e) {
            return "redirect:/auth/login";
        }
    }

    // Обновление профиля (AJAX)
    @PostMapping("/profile/update")
    @ResponseBody
    public Map<String, Object> updateProfile(@RequestBody ClientProfileDTO profileDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long clientId = getCurrentClientId();
            String oldLogin = getCurrentClientLogin();

            Client updatedClient = clientProfileService.updateClientProfile(clientId, profileDTO);

            response.put("success", true);
            response.put("message", "Профиль успешно обновлен");
            response.put("user", updatedClient);
            response.put("loginChanged", !oldLogin.equals(updatedClient.getLogin()));
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Ошибка при обновлении профиля: " + e.getMessage());
        }
        return response;
    }

    // Смена пароля (AJAX)
    @PostMapping("/profile/change-password")
    @ResponseBody
    public Map<String, Object> changePassword(@RequestParam String currentPassword,
                                              @RequestParam String newPassword,
                                              @RequestParam String confirmPassword) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long clientId = getCurrentClientId();

            if (!newPassword.equals(confirmPassword)) {
                response.put("success", false);
                response.put("message", "Новые пароли не совпадают");
                return response;
            }

            boolean passwordChanged = clientProfileService.updatePassword(clientId, currentPassword, newPassword);

            if (passwordChanged) {
                response.put("success", true);
                response.put("message", "Пароль успешно изменен");
            } else {
                response.put("success", false);
                response.put("message", "Текущий пароль введен неверно");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Ошибка при смене пароля: " + e.getMessage());
        }
        return response;
    }

    // Добавление/обновление информации об автомобиле (AJAX)
    @PostMapping("/profile/car/update")
    @ResponseBody
    public Map<String, Object> updateCarInfo(@RequestBody ClientProfileDTO carDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long clientId = getCurrentClientId();
            clientProfileService.addOrUpdateCarInfo(clientId, carDTO);

            response.put("success", true);
            response.put("message", "Информация об автомобиле успешно сохранена");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Ошибка при сохранении информации об автомобиле: " + e.getMessage());
        }
        return response;
    }

    // Получение обновленных данных профиля (AJAX)
    @GetMapping("/profile/data")
    @ResponseBody
    public Map<String, Object> getProfileData() {
        Map<String, Object> response = new HashMap<>();
        try {
            String clientLogin = getCurrentClientLogin();
            ClientProfileDTO clientProfile = clientProfileService.getClientProfileDTOByLogin(clientLogin);
            Long clientId = getCurrentClientId();

            response.put("success", true);
            response.put("profile", clientProfile);
            response.put("cartItemsCount", cartService.countItemsInCart(clientId));
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Ошибка при получении данных профиля");
        }
        return response;
    }

    // AJAX методы для корзины и заказов

    @PostMapping("/cart/add")
    @ResponseBody
    public Map<String, Object> addToCart(@RequestParam Long itemId,
                                         @RequestParam(defaultValue = "1") int quantity) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long clientId = getCurrentClientId();
            cartService.addToCart(clientId, itemId, quantity);

            response.put("success", true);
            response.put("message", "Товар успешно добавлен в корзину!");
            response.put("cartItemsCount", cartService.countItemsInCart(clientId));
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Ошибка при добавлении товара в корзину: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/cart/update")
    @ResponseBody
    public Map<String, Object> updateCart(@RequestParam Long itemId,
                                          @RequestParam int quantity) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long clientId = getCurrentClientId();
            cartService.updateQuantity(clientId, itemId, quantity);

            ClientController.CartSummary cartSummary = cartService.getCartSummary(clientId);
            double itemTotal = cartSummary.getCartItems().stream()
                    .filter(cart -> cart.getItem().getId().equals(itemId))
                    .findFirst()
                    .map(cart -> cart.getItem().getPrice() * cart.getQuantity())
                    .orElse(0.0);

            response.put("success", true);
            response.put("itemTotal", itemTotal);
            response.put("cartTotal", cartSummary.getTotalSum());
            response.put("cartItemsCount", cartSummary.getCartItemsCount());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Ошибка при обновлении корзины: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/cart/remove")
    @ResponseBody
    public Map<String, Object> removeFromCart(@RequestParam Long itemId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long clientId = getCurrentClientId();
            cartService.removeItem(clientId, itemId);

            ClientController.CartSummary cartSummary = cartService.getCartSummary(clientId);

            response.put("success", true);
            response.put("message", "Товар удален из корзины");
            response.put("cartTotal", cartSummary.getTotalSum());
            response.put("cartItemsCount", cartSummary.getCartItemsCount());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Ошибка при удалении товара: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/cart/clear")
    @ResponseBody
    public Map<String, Object> clearCart() {
        Map<String, Object> response = new HashMap<>();
        try {
            Long clientId = getCurrentClientId();
            cartService.clearCart(clientId);

            response.put("success", true);
            response.put("message", "Корзина очищена");
            response.put("cartItemsCount", 0);
            response.put("cartTotal", 0.0);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Ошибка при очистке корзины: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/order/create")
    @ResponseBody
    public Map<String, Object> createOrder(@RequestParam String paymentMethod,
                                           @RequestParam(required = false) String cardDetails) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long clientId = getCurrentClientId();
            Order order = orderService.createOrderFromCart(clientId, paymentMethod, cardDetails);

            // Получаем клиента для отправки email
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Клиент не найден"));

            // Отправляем email уведомление о заказе
            if (client.getEmail() != null && !client.getEmail().isEmpty()) {
                try {
                    emailService.sendOrderCreatedNotification(
                            client.getEmail(),
                            client.getName(),
                            order.getId(),
                            order.getTotalAmount()
                    );
                } catch (Exception emailException) {
                    // Логируем ошибку отправки email, но не прерываем выполнение
                    System.err.println("Ошибка отправки email: " + emailException.getMessage());
                }
            }

            response.put("success", true);
            response.put("message", "Заказ успешно оформлен!");
            response.put("orderId", order.getId());
            response.put("redirectUrl", "/client/orders");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Ошибка при оформлении заказа: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/order/create-direct")
    @ResponseBody
    public Map<String, Object> createDirectOrder(@RequestParam Long itemId,
                                                 @RequestParam int quantity,
                                                 @RequestParam String paymentMethod,
                                                 @RequestParam(required = false) String cardDetails) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long clientId = getCurrentClientId();
            Order order = orderService.createDirectOrder(clientId, itemId, quantity, paymentMethod, cardDetails);

            // Получаем клиента для отправки email
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Клиент не найден"));

            // Отправляем email уведомление о заказе
            if (client.getEmail() != null && !client.getEmail().isEmpty()) {
                try {
                    emailService.sendOrderCreatedNotification(
                            client.getEmail(),
                            client.getName(),
                            order.getId(),
                            order.getTotalAmount()
                    );
                } catch (Exception emailException) {
                    // Логируем ошибку отправки email, но не прерываем выполнение
                    System.err.println("Ошибка отправки email: " + emailException.getMessage());
                }
            }

            response.put("success", true);
            response.put("message", "Заказ успешно оформлен!");
            response.put("orderId", order.getId());
            response.put("redirectUrl", "/client/orders");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Ошибка при оформлении заказа: " + e.getMessage());
        }
        return response;
    }

    // Вспомогательные классы для передачи данных в шаблоны
    public static class CartSummary {
        private final List<Cart> cartItems;
        private final double totalSum;
        private final int totalItems;
        private final long cartItemsCount;

        public CartSummary(List<Cart> cartItems, double totalSum, int totalItems, long cartItemsCount) {
            this.cartItems = cartItems;
            this.totalSum = totalSum;
            this.totalItems = totalItems;
            this.cartItemsCount = cartItemsCount;
        }

        // геттеры
        public List<Cart> getCartItems() { return cartItems; }
        public double getTotalSum() { return totalSum; }
        public int getTotalItems() { return totalItems; }
        public long getCartItemsCount() { return cartItemsCount; }
    }

    public static class OrderDetails {
        private final Order order;
        private final List<OrderItem> items;
        private final OrderStatus status;

        public OrderDetails(Order order, List<OrderItem> items, OrderStatus status) {
            this.order = order;
            this.items = items;
            this.status = status;
        }

        // геттеры
        public Order getOrder() { return order; }
        public List<OrderItem> getItems() { return items; }
        public OrderStatus getStatus() { return status; }
    }
}