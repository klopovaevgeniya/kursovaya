package com.example.AutoDetail.controller.client;

import com.example.AutoDetail.entity.*;
import com.example.AutoDetail.repository.*;
import com.example.AutoDetail.service.CartService;
import com.example.AutoDetail.service.ClientProfileService;
import com.example.AutoDetail.service.ItemService;
import com.example.AutoDetail.service.OrderService;
import com.example.AutoDetail.service.EmailService;
import com.example.AutoDetail.service.UserService;
import com.example.AutoDetail.service.SearchHistoryService;
import com.example.AutoDetail.dto.ClientProfileDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    private final ItemService itemService;
    private final CartService cartService;
    private final OrderService orderService;
    private final CategoryRepository categoryRepository;
    private final ClientRepository clientRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final ClientProfileService clientProfileService;
    private final EmailService emailService;
    private final UserService userService;
    private final SearchHistoryService searchHistoryService;

    public ClientController(ItemService itemService, CartService cartService,
                            OrderService orderService, CategoryRepository categoryRepository,
                            ClientRepository clientRepository, OrderStatusRepository orderStatusRepository,
                            ClientProfileService clientProfileService, EmailService emailService,
                            UserService userService, SearchHistoryService searchHistoryService) {
        this.itemService = itemService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.categoryRepository = categoryRepository;
        this.clientRepository = clientRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.clientProfileService = clientProfileService;
        this.emailService = emailService;
        this.userService = userService;
        this.searchHistoryService = searchHistoryService;
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
                logger.warn("Клиент не найден для пользователя: {}", username);
                throw new RuntimeException("Клиент не найден для пользователя: " + username);
            }
        }
        logger.warn("Попытка доступа неаутентифицированным пользователем");
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
                logger.warn("Клиент не найден для пользователя: {}", username);
                throw new RuntimeException("Клиент не найден для пользователя: " + username);
            }
        }
        logger.warn("Попытка доступа неаутентифицированным пользователем");
        throw new RuntimeException("Пользователь не аутентифицирован");
    }

    // Каталог с поиском, фильтрацией, сортировкой и историей поиска
    @GetMapping("/catalog")
    public String catalog(@RequestParam(required = false) String search,
                          @RequestParam(required = false) Long category,
                          @RequestParam(required = false) String sort,
                          Model model) {

        logger.info("Запрос каталога: search={}, category={}, sort={}", search, category, sort);
        try {
            Long clientId = getCurrentClientId();
            Client currentClient = getCurrentClient();
            List<Item> items = itemService.getFilteredAndSortedItems(search, category, sort);

            // Сохраняем поиск в историю, если есть поисковый запрос
            if (search != null && !search.trim().isEmpty()) {
                logger.debug("Сохранение поиска в историю: {}", search);
                searchHistoryService.saveSearch(currentClient, search);
            }

            // Получаем историю поиска для выпадающего списка
            List<String> searchHistory = searchHistoryService.getSearchHistory(clientId);

            model.addAttribute("items", items);
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("search", search);
            model.addAttribute("selectedCategory", category);
            model.addAttribute("selectedSort", sort);
            model.addAttribute("cartItemsCount", cartService.countItemsInCart(clientId));
            model.addAttribute("currentClientLogin", currentClient.getLogin());
            model.addAttribute("searchHistory", searchHistory);

            logger.info("Каталог успешно загружен: {} товаров, история поиска: {} записей",
                    items.size(), searchHistory.size());
            return "client/catalog";
        } catch (RuntimeException e) {
            logger.warn("Ошибка доступа к каталогу, перенаправление на логин: {}", e.getMessage());
            return "redirect:/auth/login";
        }
    }

    // AJAX метод для получения истории поиска
    @GetMapping("/search-history")
    @ResponseBody
    public List<String> getSearchHistory() {
        logger.debug("AJAX запрос истории поиска");
        try {
            Long clientId = getCurrentClientId();
            List<String> history = searchHistoryService.getSearchHistory(clientId);
            logger.debug("История поиска загружена: {} записей", history.size());
            return history;
        } catch (Exception e) {
            logger.error("Ошибка получения истории поиска: {}", e.getMessage(), e);
            return List.of();
        }
    }

    // Страница связи с менеджерами
    @GetMapping("/contact-managers")
    public String contactManagers(@RequestParam(required = false) String search,
                                  @RequestParam(required = false) String sort,
                                  Model model) {
        logger.info("Запрос страницы менеджеров: search={}, sort={}", search, sort);
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
                logger.debug("Применен поиск менеджеров: '{}', найдено: {}", search, managers.size());
            }

            // Применяем сортировку
            if (sort != null && !sort.isEmpty()) {
                logger.debug("Применена сортировка менеджеров: {}", sort);
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

            logger.info("Страница менеджеров загружена: {} менеджеров", managers.size());
            return "client/contact-managers";
        } catch (RuntimeException e) {
            logger.warn("Ошибка доступа к странице менеджеров, перенаправление на логин: {}", e.getMessage());
            return "redirect:/auth/login";
        }
    }

    // Детальная страница товара
    @GetMapping("/item/{id}")
    public String itemDetails(@PathVariable Long id, Model model) {
        logger.info("Запрос детальной страницы товара ID={}", id);
        try {
            Long clientId = getCurrentClientId();
            Client currentClient = getCurrentClient();
            Item item = itemService.getItemById(id);
            if (item == null) {
                logger.warn("Товар с ID={} не найден", id);
                return "redirect:/client/catalog";
            }

            List<Item> relatedItems = itemService.getRelatedItems(item);

            model.addAttribute("item", item);
            model.addAttribute("relatedItems", relatedItems);
            model.addAttribute("cartItemsCount", cartService.countItemsInCart(clientId));
            model.addAttribute("currentClientLogin", currentClient.getLogin());

            logger.debug("Детальная страница товара '{}' загружена, связанных товаров: {}",
                    item.getName(), relatedItems.size());
            return "client/item-details";
        } catch (RuntimeException e) {
            logger.warn("Ошибка доступа к детальной странице товара, перенаправление на логин: {}", e.getMessage());
            return "redirect:/auth/login";
        }
    }

    // Просмотр корзины
    @GetMapping("/cart")
    public String viewCart(Model model) {
        logger.info("Запрос страницы корзины");
        try {
            Long clientId = getCurrentClientId();
            Client currentClient = getCurrentClient();
            ClientController.CartSummary cartSummary = cartService.getCartSummary(clientId);

            model.addAttribute("cartItems", cartSummary.getCartItems());
            model.addAttribute("totalSum", cartSummary.getTotalSum());
            model.addAttribute("totalItems", cartSummary.getTotalItems());
            model.addAttribute("cartItemsCount", cartSummary.getCartItemsCount());
            model.addAttribute("currentClientLogin", currentClient.getLogin());

            logger.info("Корзина загружена: {} товаров, общая сумма: {}",
                    cartSummary.getTotalItems(), cartSummary.getTotalSum());
            return "client/cart";
        } catch (RuntimeException e) {
            logger.warn("Ошибка доступа к корзине, перенаправление на логин: {}", e.getMessage());
            return "redirect:/auth/login";
        }
    }

    // Страница оформления заказа из корзины
    @GetMapping("/order/checkout")
    public String checkout(Model model) {
        logger.info("Запрос страницы оформления заказа из корзины");
        try {
            Long clientId = getCurrentClientId();
            Client currentClient = getCurrentClient();
            ClientController.CartSummary cartSummary = cartService.getCartSummary(clientId);

            if (cartSummary.getCartItems().isEmpty()) {
                logger.warn("Попытка оформления заказа с пустой корзиной");
                return "redirect:/client/cart";
            }

            model.addAttribute("cartItems", cartSummary.getCartItems());
            model.addAttribute("totalSum", cartSummary.getTotalSum());
            model.addAttribute("cartItemsCount", cartSummary.getCartItemsCount());
            model.addAttribute("currentClientLogin", currentClient.getLogin());

            logger.debug("Страница оформления заказа загружена: {} товаров", cartSummary.getTotalItems());
            return "client/checkout";
        } catch (RuntimeException e) {
            logger.warn("Ошибка доступа к странице оформления заказа, перенаправление на логин: {}", e.getMessage());
            return "redirect:/auth/login";
        }
    }

    // Страница оформления заказа с детальной страницы
    @GetMapping("/order/checkout-direct")
    public String checkoutDirect(@RequestParam Long itemId,
                                 @RequestParam int quantity,
                                 Model model) {
        logger.info("Запрос прямого оформления заказа: itemId={}, quantity={}", itemId, quantity);
        try {
            Long clientId = getCurrentClientId();
            Client currentClient = getCurrentClient();
            Item item = itemService.getItemById(itemId);
            if (item == null) {
                logger.warn("Товар с ID={} не найден при прямом оформлении", itemId);
                return "redirect:/client/catalog";
            }

            double totalSum = item.getPrice() * quantity;

            model.addAttribute("item", item);
            model.addAttribute("quantity", quantity);
            model.addAttribute("totalSum", totalSum);
            model.addAttribute("cartItemsCount", cartService.countItemsInCart(clientId));
            model.addAttribute("currentClientLogin", currentClient.getLogin());

            logger.debug("Страница прямого оформления заказа загружена для товара '{}'", item.getName());
            return "client/checkout-direct";
        } catch (RuntimeException e) {
            logger.warn("Ошибка доступа к странице прямого оформления, перенаправление на логин: {}", e.getMessage());
            return "redirect:/auth/login";
        }
    }

    // Мои заказы
    @GetMapping("/orders")
    public String orders(Model model) {
        logger.info("Запрос страницы заказов");
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

            logger.info("Страница заказов загружена: {} заказов", orderDetails.size());
            return "client/orders";
        } catch (RuntimeException e) {
            logger.warn("Ошибка доступа к странице заказов, перенаправление на логин: {}", e.getMessage());
            return "redirect:/auth/login";
        }
    }

    // Детали заказа
    @GetMapping("/order/{id}")
    public String orderDetails(@PathVariable Long id, Model model) {
        logger.info("Запрос деталей заказа ID={}", id);
        try {
            Long clientId = getCurrentClientId();
            Client currentClient = getCurrentClient();
            Order order = orderService.getOrderById(id)
                    .orElse(null);

            if (order == null || !order.getClientId().equals(clientId)) {
                logger.warn("Попытка доступа к несуществующему или чужому заказу ID={}", id);
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

            logger.debug("Детали заказа ID={} загружены: {} позиций", id, orderItems.size());
            return "client/order-details";
        } catch (RuntimeException e) {
            logger.warn("Ошибка доступа к деталям заказа, перенаправление на логин: {}", e.getMessage());
            return "redirect:/auth/login";
        }
    }

    // === ПРОФИЛЬ КЛИЕНТA ===

    // Страница профиля
    @GetMapping("/profile")
    public String profile(Model model) {
        logger.info("Запрос страницы профиля");
        try {
            String clientLogin = getCurrentClientLogin();
            Client currentClient = getCurrentClient();
            ClientProfileDTO clientProfile = clientProfileService.getClientProfileDTOByLogin(clientLogin);
            Long clientId = getCurrentClientId();

            model.addAttribute("user", clientProfile);
            model.addAttribute("clientProfile", clientProfile);
            model.addAttribute("cartItemsCount", cartService.countItemsInCart(clientId));
            model.addAttribute("currentClientLogin", currentClient.getLogin());

            logger.debug("Страница профиля загружена для клиента: {}", clientLogin);
            return "client/profile";
        } catch (RuntimeException e) {
            logger.warn("Ошибка доступа к профилю, перенаправление на логин: {}", e.getMessage());
            return "redirect:/auth/login";
        }
    }

    // Обновление профиля (AJAX)
    @PostMapping("/profile/update")
    @ResponseBody
    public Map<String, Object> updateProfile(@RequestBody ClientProfileDTO profileDTO) {
        logger.info("AJAX запрос обновления профиля");
        Map<String, Object> response = new HashMap<>();
        try {
            Long clientId = getCurrentClientId();
            String oldLogin = getCurrentClientLogin();

            Client updatedClient = clientProfileService.updateClientProfile(clientId, profileDTO);

            response.put("success", true);
            response.put("message", "Профиль успешно обновлен");
            response.put("user", updatedClient);
            response.put("loginChanged", !oldLogin.equals(updatedClient.getLogin()));

            logger.info("Профиль клиента ID={} успешно обновлен", clientId);
        } catch (Exception e) {
            logger.error("Ошибка обновления профиля: {}", e.getMessage(), e);
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
        logger.info("AJAX запрос смены пароля");
        Map<String, Object> response = new HashMap<>();
        try {
            Long clientId = getCurrentClientId();

            if (!newPassword.equals(confirmPassword)) {
                logger.warn("Пароли не совпадают при смене пароля для клиента ID={}", clientId);
                response.put("success", false);
                response.put("message", "Новые пароли не совпадают");
                return response;
            }

            boolean passwordChanged = clientProfileService.updatePassword(clientId, currentPassword, newPassword);

            if (passwordChanged) {
                logger.info("Пароль успешно изменен для клиента ID={}", clientId);
                response.put("success", true);
                response.put("message", "Пароль успешно изменен");
            } else {
                logger.warn("Неверный текущий пароль для клиента ID={}", clientId);
                response.put("success", false);
                response.put("message", "Текущий пароль введен неверно");
            }
        } catch (Exception e) {
            logger.error("Ошибка смены пароля: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Ошибка при смене пароля: " + e.getMessage());
        }
        return response;
    }

    // Добавление/обновление информации об автомобиле (AJAX)
    @PostMapping("/profile/car/update")
    @ResponseBody
    public Map<String, Object> updateCarInfo(@RequestBody ClientProfileDTO carDTO) {
        logger.info("AJAX запрос обновления информации об автомобиле");
        Map<String, Object> response = new HashMap<>();
        try {
            Long clientId = getCurrentClientId();
            clientProfileService.addOrUpdateCarInfo(clientId, carDTO);

            response.put("success", true);
            response.put("message", "Информация об автомобиле успешно сохранена");

            logger.debug("Информация об автомобиле обновлена для клиента ID={}", clientId);
        } catch (Exception e) {
            logger.error("Ошибка обновления информации об автомобиле: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Ошибка при сохранении информации об автомобиле: " + e.getMessage());
        }
        return response;
    }

    // Получение обновленных данных профиля (AJAX)
    @GetMapping("/profile/data")
    @ResponseBody
    public Map<String, Object> getProfileData() {
        logger.debug("AJAX запрос данных профиля");
        Map<String, Object> response = new HashMap<>();
        try {
            String clientLogin = getCurrentClientLogin();
            ClientProfileDTO clientProfile = clientProfileService.getClientProfileDTOByLogin(clientLogin);
            Long clientId = getCurrentClientId();

            response.put("success", true);
            response.put("profile", clientProfile);
            response.put("cartItemsCount", cartService.countItemsInCart(clientId));

            logger.debug("Данные профиля загружены для клиента: {}", clientLogin);
        } catch (Exception e) {
            logger.error("Ошибка получения данных профиля: {}", e.getMessage(), e);
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
        logger.info("AJAX запрос добавления в корзину: itemId={}, quantity={}", itemId, quantity);
        Map<String, Object> response = new HashMap<>();
        try {
            Long clientId = getCurrentClientId();
            cartService.addToCart(clientId, itemId, quantity);

            response.put("success", true);
            response.put("message", "Товар успешно добавлен в корзину!");
            response.put("cartItemsCount", cartService.countItemsInCart(clientId));

            logger.debug("Товар ID={} добавлен в корзину клиента ID={}", itemId, clientId);
        } catch (Exception e) {
            logger.error("Ошибка добавления в корзину: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Ошибка при добавлении товара в корзину: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/cart/update")
    @ResponseBody
    public Map<String, Object> updateCart(@RequestParam Long itemId,
                                          @RequestParam int quantity) {
        logger.info("AJAX запрос обновления корзины: itemId={}, quantity={}", itemId, quantity);
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

            logger.debug("Корзина обновлена для клиента ID={}: товар ID={}, количество={}",
                    clientId, itemId, quantity);
        } catch (Exception e) {
            logger.error("Ошибка обновления корзины: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Ошибка при обновлении корзины: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/cart/remove")
    @ResponseBody
    public Map<String, Object> removeFromCart(@RequestParam Long itemId) {
        logger.info("AJAX запрос удаления из корзины: itemId={}", itemId);
        Map<String, Object> response = new HashMap<>();
        try {
            Long clientId = getCurrentClientId();
            cartService.removeItem(clientId, itemId);

            ClientController.CartSummary cartSummary = cartService.getCartSummary(clientId);

            response.put("success", true);
            response.put("message", "Товар удален из корзины");
            response.put("cartTotal", cartSummary.getTotalSum());
            response.put("cartItemsCount", cartSummary.getCartItemsCount());

            logger.debug("Товар ID={} удален из корзины клиента ID={}", itemId, clientId);
        } catch (Exception e) {
            logger.error("Ошибка удаления из корзины: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Ошибка при удалении товара: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/cart/clear")
    @ResponseBody
    public Map<String, Object> clearCart() {
        logger.info("AJAX запрос очистки корзины");
        Map<String, Object> response = new HashMap<>();
        try {
            Long clientId = getCurrentClientId();
            cartService.clearCart(clientId);

            response.put("success", true);
            response.put("message", "Корзина очищена");
            response.put("cartItemsCount", 0);
            response.put("cartTotal", 0.0);

            logger.info("Корзина очищена для клиента ID={}", clientId);
        } catch (Exception e) {
            logger.error("Ошибка очистки корзины: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Ошибка при очистке корзины: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/order/create")
    @ResponseBody
    public Map<String, Object> createOrder(@RequestParam String paymentMethod,
                                           @RequestParam(required = false) String cardDetails) {
        logger.info("AJAX запрос создания заказа из корзины: paymentMethod={}", paymentMethod);
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
                    logger.debug("Email уведомление отправлено для заказа ID={}", order.getId());
                } catch (Exception emailException) {
                    logger.warn("Ошибка отправки email для заказа ID={}: {}",
                            order.getId(), emailException.getMessage());
                }
            }

            response.put("success", true);
            response.put("message", "Заказ успешно оформлен!");
            response.put("orderId", order.getId());
            response.put("redirectUrl", "/client/orders");

            logger.info("Заказ успешно создан из корзины: ID={}, сумма={}",
                    order.getId(), order.getTotalAmount());
        } catch (Exception e) {
            logger.error("Ошибка создания заказа из корзины: {}", e.getMessage(), e);
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
        logger.info("AJAX запрос прямого создания заказа: itemId={}, quantity={}, paymentMethod={}",
                itemId, quantity, paymentMethod);
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
                    logger.debug("Email уведомление отправлено для прямого заказа ID={}", order.getId());
                } catch (Exception emailException) {
                    logger.warn("Ошибка отправки email для прямого заказа ID={}: {}",
                            order.getId(), emailException.getMessage());
                }
            }

            response.put("success", true);
            response.put("message", "Заказ успешно оформлен!");
            response.put("orderId", order.getId());
            response.put("redirectUrl", "/client/orders");

            logger.info("Прямой заказ успешно создан: ID={}, товар ID={}, количество={}",
                    order.getId(), itemId, quantity);
        } catch (Exception e) {
            logger.error("Ошибка создания прямого заказа: {}", e.getMessage(), e);
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