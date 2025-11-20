package com.example.AutoDetail.controller.admin;

import com.example.AutoDetail.entity.*;
import com.example.AutoDetail.service.AdminService;
import com.example.AutoDetail.service.ExportImportService;
import com.example.AutoDetail.service.ReportService;
import com.example.AutoDetail.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;
    private final ExportImportService exportImportService;
    private final ReportService reportService;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    public AdminController(AdminService adminService,
                           ExportImportService exportImportService,
                           ReportService reportService,
                           OrderRepository orderRepository,
                           ItemRepository itemRepository,
                           UserRepository userRepository,
                           ClientRepository clientRepository) {
        this.adminService = adminService;
        this.exportImportService = exportImportService;
        this.reportService = reportService;
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
    }

    // === ГЛАВНАЯ ПАНЕЛЬ ===
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        logger.info("Запрос главной панели администратора");
        try {
            long totalItems = adminService.getTotalItems();
            long totalSuppliers = adminService.getTotalSuppliers();
            long totalManagers = adminService.getTotalManagers();
            long totalClients = adminService.getTotalClients();
            long totalCategories = adminService.getTotalCategories();

            logger.debug("Статистика загружена: товары={}, поставщики={}, менеджеры={}, клиенты={}, категории={}",
                    totalItems, totalSuppliers, totalManagers, totalClients, totalCategories);

            model.addAttribute("title", "Панель администратора");
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("totalSuppliers", totalSuppliers);
            model.addAttribute("totalManagers", totalManagers);
            model.addAttribute("totalClients", totalClients);
            model.addAttribute("totalCategories", totalCategories);

            logger.info("Главная панель успешно загружена");
            return "admin/dashboard";
        } catch (Exception e) {
            logger.error("Ошибка загрузки dashboard: {}", e.getMessage(), e);
            model.addAttribute("error", "Ошибка загрузки dashboard: " + e.getMessage());
            return "error";
        }
    }

    // === ТОВАРЫ ===
    @GetMapping("/items")
    public String itemsPage(@RequestParam(value = "search", required = false) String search,
                            Model model) {
        logger.info("Запрос страницы товаров, поиск: {}", search != null ? search : "нет");
        try {
            List<Item> items;
            if (search != null && !search.isEmpty()) {
                items = adminService.searchItems(search);
                model.addAttribute("searchType", "результаты поиска: " + search);
                logger.debug("Найдено товаров по поиску '{}': {}", search, items.size());
            } else {
                items = adminService.getAllItems();
                model.addAttribute("searchType", "все товары");
                logger.debug("Загружено всех товаров: {}", items.size());
            }
            model.addAttribute("items", items);
            model.addAttribute("search", search);
            model.addAttribute("totalItems", items.size());
            logger.info("Страница товаров успешно загружена");
            return "admin/items";
        } catch (Exception e) {
            logger.error("Ошибка загрузки товаров: {}", e.getMessage(), e);
            model.addAttribute("error", "Ошибка загрузки товаров: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/items/create")
    public String createItemForm(Model model) {
        logger.info("Запрос формы создания товара");
        try {
            List<Supplier> suppliers = adminService.getAllSuppliers();
            List<Category> categories = adminService.getAllCategories();
            model.addAttribute("item", new Item());
            model.addAttribute("suppliers", suppliers);
            model.addAttribute("categories", categories);
            model.addAttribute("isEdit", false);
            logger.debug("Форма создания товара загружена, поставщиков: {}, категорий: {}", suppliers.size(), categories.size());
            return "admin/item-form";
        } catch (Exception e) {
            logger.error("Ошибка загрузки формы создания товара: {}", e.getMessage(), e);
            model.addAttribute("error", "Ошибка загрузки формы: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/items")
    public String createItem(@ModelAttribute Item item,
                             @RequestParam("supplier.id") Long supplierId,
                             @RequestParam("category.id") Long categoryId,
                             @RequestParam(value = "imageUrl", required = false) String imageUrl,
                             RedirectAttributes redirectAttributes) {
        logger.info("Создание нового товара: название={}, поставщик={}, категория={}",
                item.getName(), supplierId, categoryId);
        try {
            adminService.saveItemWithImageUrl(item, supplierId, categoryId, imageUrl);
            logger.info("Товар успешно создан: ID={}, название={}", item.getId(), item.getName());
            redirectAttributes.addFlashAttribute("success", "Товар успешно создан");
        } catch (Exception e) {
            logger.error("Ошибка создания товара: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/items";
    }

    @GetMapping("/items/edit/{id}")
    public String editItemForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Запрос формы редактирования товара ID={}", id);
        try {
            Optional<Item> itemOpt = adminService.getItemById(id);
            if (itemOpt.isPresent()) {
                List<Supplier> suppliers = adminService.getAllSuppliers();
                List<Category> categories = adminService.getAllCategories();
                model.addAttribute("item", itemOpt.get());
                model.addAttribute("suppliers", suppliers);
                model.addAttribute("categories", categories);
                model.addAttribute("isEdit", true);
                logger.debug("Форма редактирования товара ID={} загружена", id);
                return "admin/item-form";
            }
            logger.warn("Товар с ID={} не найден", id);
            redirectAttributes.addFlashAttribute("error", "Товар не найден");
            return "redirect:/admin/items";
        } catch (Exception e) {
            logger.error("Ошибка загрузки формы редактирования товара ID={}: {}", id, e.getMessage(), e);
            model.addAttribute("error", "Ошибка загрузки формы: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/items/update/{id}")
    public String updateItem(@PathVariable Long id,
                             @ModelAttribute Item item,
                             @RequestParam("supplier.id") Long supplierId,
                             @RequestParam("category.id") Long categoryId,
                             @RequestParam(value = "imageUrl", required = false) String imageUrl,
                             RedirectAttributes redirectAttributes) {
        logger.info("Обновление товара ID={}: название={}", id, item.getName());
        try {
            item.setId(id);
            adminService.saveItemWithImageUrl(item, supplierId, categoryId, imageUrl);
            logger.info("Товар ID={} успешно обновлен", id);
            redirectAttributes.addFlashAttribute("success", "Товар успешно обновлен");
        } catch (Exception e) {
            logger.error("Ошибка обновления товара ID={}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/items";
    }

    @PostMapping("/items/delete/{id}")
    public String deleteItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Удаление товара ID={}", id);
        try {
            adminService.deleteItem(id);
            logger.info("Товар ID={} успешно удален", id);
            redirectAttributes.addFlashAttribute("success", "Товар успешно удален");
            return "redirect:/admin/items";
        } catch (Exception e) {
            String cleanErrorMessage = e.getMessage().replaceAll("[\\r\\n]", " ").trim();
            logger.error("Ошибка удаления товара ID={}: {}", id, cleanErrorMessage, e);
            redirectAttributes.addFlashAttribute("error", cleanErrorMessage);
            return "redirect:/admin/items";
        }
    }

    // === ПОСТАВЩИКИ ===
    @GetMapping("/suppliers")
    public String suppliersPage(@RequestParam(value = "search", required = false) String search,
                                Model model) {
        logger.info("Запрос страницы поставщиков, поиск: {}", search != null ? search : "нет");
        try {
            List<Supplier> suppliers;
            if (search != null && !search.isEmpty()) {
                suppliers = adminService.searchSuppliers(search);
                model.addAttribute("searchType", "результаты поиска: " + search);
                logger.debug("Найдено поставщиков по поиску '{}': {}", search, suppliers.size());
            } else {
                suppliers = adminService.getAllSuppliers();
                model.addAttribute("searchType", "все поставщики");
                logger.debug("Загружено всех поставщиков: {}", suppliers.size());
            }
            model.addAttribute("suppliers", suppliers);
            model.addAttribute("search", search);
            model.addAttribute("totalSuppliers", suppliers.size());
            logger.info("Страница поставщиков успешно загружена");
            return "admin/suppliers";
        } catch (Exception e) {
            logger.error("Ошибка загрузки поставщиков: {}", e.getMessage(), e);
            model.addAttribute("error", "Ошибка загрузки поставщиков: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/suppliers/create")
    public String createSupplierForm(Model model) {
        logger.info("Запрос формы создания поставщика");
        model.addAttribute("supplier", new Supplier());
        model.addAttribute("isEdit", false);
        return "admin/supplier-form";
    }

    @PostMapping("/suppliers")
    public String createSupplier(@ModelAttribute Supplier supplier, RedirectAttributes redirectAttributes) {
        logger.info("Создание нового поставщика: название={}", supplier.getName());
        try {
            adminService.saveSupplier(supplier);
            logger.info("Поставщик успешно создан: ID={}, название={}", supplier.getId(), supplier.getName());
            redirectAttributes.addFlashAttribute("success", "Поставщик успешно создан");
        } catch (Exception e) {
            logger.error("Ошибка создания поставщика: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/suppliers";
    }

    @GetMapping("/suppliers/edit/{id}")
    public String editSupplierForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Запрос формы редактирования поставщика ID={}", id);
        try {
            Optional<Supplier> supplierOpt = adminService.getSupplierById(id);
            if (supplierOpt.isPresent()) {
                model.addAttribute("supplier", supplierOpt.get());
                model.addAttribute("isEdit", true);
                logger.debug("Форма редактирования поставщика ID={} загружена", id);
                return "admin/supplier-form";
            }
            logger.warn("Поставщик с ID={} не найден", id);
            redirectAttributes.addFlashAttribute("error", "Поставщик не найден");
            return "redirect:/admin/suppliers";
        } catch (Exception e) {
            logger.error("Ошибка загрузки формы редактирования поставщика ID={}: {}", id, e.getMessage(), e);
            model.addAttribute("error", "Ошибка загрузки формы: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/suppliers/update/{id}")
    public String updateSupplier(@PathVariable Long id, @ModelAttribute Supplier supplier, RedirectAttributes redirectAttributes) {
        logger.info("Обновление поставщика ID={}: название={}", id, supplier.getName());
        try {
            supplier.setId(id);
            adminService.saveSupplier(supplier);
            logger.info("Поставщик ID={} успешно обновлен", id);
            redirectAttributes.addFlashAttribute("success", "Поставщик успешно обновлен");
        } catch (Exception e) {
            logger.error("Ошибка обновления поставщика ID={}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/suppliers";
    }

    @PostMapping("/suppliers/delete/{id}")
    public String deleteSupplier(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Удаление поставщика ID={}", id);
        try {
            adminService.deleteSupplier(id);
            logger.info("Поставщик ID={} успешно удален", id);
            redirectAttributes.addFlashAttribute("success", "Поставщик успешно удален");
        } catch (Exception e) {
            logger.error("Ошибка удаления поставщика ID={}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/suppliers";
    }

    // === КАТЕГОРИИ ===
    @GetMapping("/categories")
    public String categoriesPage(@RequestParam(value = "search", required = false) String search,
                                 Model model) {
        logger.info("Запрос страницы категорий, поиск: {}", search != null ? search : "нет");
        try {
            List<Category> categories;
            if (search != null && !search.isEmpty()) {
                categories = adminService.searchCategories(search);
                model.addAttribute("searchType", "результаты поиска: " + search);
                logger.debug("Найдено категорий по поиску '{}': {}", search, categories.size());
            } else {
                categories = adminService.getAllCategories();
                model.addAttribute("searchType", "все категории");
                logger.debug("Загружено всех категорий: {}", categories.size());
            }
            model.addAttribute("categories", categories);
            model.addAttribute("search", search);
            model.addAttribute("totalCategories", categories.size());
            logger.info("Страница категорий успешно загружена");
            return "admin/categories";
        } catch (Exception e) {
            logger.error("Ошибка загрузки категорий: {}", e.getMessage(), e);
            model.addAttribute("error", "Ошибка загрузки категорий: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/categories/create")
    public String createCategoryForm(Model model) {
        logger.info("Запрос формы создания категории");
        model.addAttribute("category", new Category());
        model.addAttribute("isEdit", false);
        return "admin/category-form";
    }

    @PostMapping("/categories")
    public String createCategory(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        logger.info("Создание новой категории: название={}", category.getName());
        try {
            adminService.saveCategory(category);
            logger.info("Категория успешно создана: ID={}, название={}", category.getId(), category.getName());
            redirectAttributes.addFlashAttribute("success", "Категория успешно создана");
        } catch (Exception e) {
            logger.error("Ошибка создания категории: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Запрос формы редактирования категории ID={}", id);
        try {
            Optional<Category> categoryOpt = adminService.getCategoryById(id);
            if (categoryOpt.isPresent()) {
                model.addAttribute("category", categoryOpt.get());
                model.addAttribute("isEdit", true);
                logger.debug("Форма редактирования категории ID={} загружена", id);
                return "admin/category-form";
            }
            logger.warn("Категория с ID={} не найден", id);
            redirectAttributes.addFlashAttribute("error", "Категория не найдена");
            return "redirect:/admin/categories";
        } catch (Exception e) {
            logger.error("Ошибка загрузки формы редактирования категории ID={}: {}", id, e.getMessage(), e);
            model.addAttribute("error", "Ошибка загрузки формы: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/categories/update/{id}")
    public String updateCategory(@PathVariable Long id, @ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        logger.info("Обновление категории ID={}: название={}", id, category.getName());
        try {
            category.setId(id);
            adminService.saveCategory(category);
            logger.info("Категория ID={} успешно обновлена", id);
            redirectAttributes.addFlashAttribute("success", "Категория успешно обновлена");
        } catch (Exception e) {
            logger.error("Ошибка обновления категории ID={}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Удаление категории ID={}", id);
        try {
            adminService.deleteCategory(id);
            logger.info("Категория ID={} успешно удалена", id);
            redirectAttributes.addFlashAttribute("success", "Категория успешно удалена");
        } catch (Exception e) {
            logger.error("Ошибка удаления категории ID={}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    // === МЕНЕДЖЕРЫ ===
    @GetMapping("/managers")
    public String managersPage(@RequestParam(value = "search", required = false) String search,
                               Model model) {
        logger.info("Запрос страницы менеджеров, поиск: {}", search != null ? search : "нет");
        try {
            List<User> managers;
            if (search != null && !search.isEmpty()) {
                managers = adminService.searchManagers(search);
                model.addAttribute("searchType", "результаты поиска: " + search);
                logger.debug("Найдено менеджеров по поиску '{}': {}", search, managers.size());
            } else {
                managers = adminService.getAllManagers();
                model.addAttribute("searchType", "все менеджеры");
                logger.debug("Загружено всех менеджеров: {}", managers.size());
            }
            model.addAttribute("managers", managers);
            model.addAttribute("search", search);
            model.addAttribute("totalManagers", managers.size());
            logger.info("Страница менеджеров успешно загружена");
            return "admin/managers";
        } catch (Exception e) {
            logger.error("Ошибка загрузки менеджеров: {}", e.getMessage(), e);
            model.addAttribute("error", "Ошибка загрузки менеджеров: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/managers/create")
    public String createManagerForm(Model model) {
        logger.info("Запрос формы создания менеджера");
        model.addAttribute("manager", new User());
        model.addAttribute("isEdit", false);
        return "admin/manager-form";
    }

    @PostMapping("/managers")
    public String createManager(@ModelAttribute User manager, RedirectAttributes redirectAttributes) {
        logger.info("Создание нового менеджера: логин={}, имя={}, фамилия={}",
                manager.getLogin(), manager.getName(), manager.getSurname());
        try {
            // Базовая проверка обязательных полей
            if (manager.getName() == null || manager.getName().trim().isEmpty()) {
                logger.warn("Попытка создания менеджера без имени");
                redirectAttributes.addFlashAttribute("error", "Имя обязательно");
                return "redirect:/admin/managers/create";
            }
            if (manager.getSurname() == null || manager.getSurname().trim().isEmpty()) {
                logger.warn("Попытка создания менеджера без фамилии");
                redirectAttributes.addFlashAttribute("error", "Фамилия обязательна");
                return "redirect:/admin/managers/create";
            }
            if (manager.getLogin() == null || manager.getLogin().trim().isEmpty()) {
                logger.warn("Попытка создания менеджера без логина");
                redirectAttributes.addFlashAttribute("error", "Логин обязателен");
                return "redirect:/admin/managers/create";
            }
            if (manager.getPassword() == null || manager.getPassword().trim().isEmpty()) {
                logger.warn("Попытка создания менеджера без пароля");
                redirectAttributes.addFlashAttribute("error", "Пароль обязателен");
                return "redirect:/admin/managers/create";
            }

            // Проверка уникальности логина
            if (adminService.isLoginExists(manager.getLogin(), null)) {
                logger.warn("Попытка создания менеджера с существующим логином: {}", manager.getLogin());
                redirectAttributes.addFlashAttribute("error", "Логин уже существует");
                return "redirect:/admin/managers/create";
            }

            // Сохранение менеджера
            adminService.saveManager(manager);
            logger.info("Менеджер успешно создан: ID={}, логин={}", manager.getId(), manager.getLogin());
            redirectAttributes.addFlashAttribute("success", "Менеджер успешно создан");

        } catch (Exception e) {
            logger.error("Ошибка создания менеджера: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Ошибка создания менеджера: " + e.getMessage());
        }
        return "redirect:/admin/managers";
    }

    @GetMapping("/managers/edit/{id}")
    public String editManagerForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Запрос формы редактирования менеджера ID={}", id);
        try {
            Optional<User> managerOpt = adminService.getManagerById(id);
            if (managerOpt.isPresent()) {
                model.addAttribute("manager", managerOpt.get());
                model.addAttribute("isEdit", true);
                logger.debug("Форма редактирования менеджера ID={} загружена", id);
                return "admin/manager-form";
            }
            logger.warn("Менеджер с ID={} не найден", id);
            redirectAttributes.addFlashAttribute("error", "Менеджер не найден");
            return "redirect:/admin/managers";
        } catch (Exception e) {
            logger.error("Ошибка загрузки формы редактирования менеджера ID={}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Ошибка загрузки формы: " + e.getMessage());
            return "redirect:/admin/managers";
        }
    }

    @PostMapping("/managers/update/{id}")
    public String updateManager(@PathVariable Long id, @ModelAttribute User manager, RedirectAttributes redirectAttributes) {
        logger.info("Обновление менеджера ID={}: логин={}", id, manager.getLogin());
        try {
            // Базовая проверка обязательных полей
            if (manager.getName() == null || manager.getName().trim().isEmpty()) {
                logger.warn("Попытка обновления менеджера ID={} без имени", id);
                redirectAttributes.addFlashAttribute("error", "Имя обязательно");
                return "redirect:/admin/managers/edit/" + id;
            }
            if (manager.getSurname() == null || manager.getSurname().trim().isEmpty()) {
                logger.warn("Попытка обновления менеджера ID={} без фамилии", id);
                redirectAttributes.addFlashAttribute("error", "Фамилия обязательна");
                return "redirect:/admin/managers/edit/" + id;
            }
            if (manager.getLogin() == null || manager.getLogin().trim().isEmpty()) {
                logger.warn("Попытка обновления менеджера ID={} без логина", id);
                redirectAttributes.addFlashAttribute("error", "Логин обязателен");
                return "redirect:/admin/managers/edit/" + id;
            }

            // Проверка уникальности логина
            if (adminService.isLoginExists(manager.getLogin(), id)) {
                logger.warn("Попытка обновления менеджера ID={} на существующий логин: {}", id, manager.getLogin());
                redirectAttributes.addFlashAttribute("error", "Логин уже существует");
                return "redirect:/admin/managers/edit/" + id;
            }

            manager.setId(id);
            adminService.saveManager(manager);
            logger.info("Менеджер ID={} успешно обновлен", id);
            redirectAttributes.addFlashAttribute("success", "Менеджер успешно обновлен");

        } catch (Exception e) {
            logger.error("Ошибка обновления менеджера ID={}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Ошибка обновления менеджера: " + e.getMessage());
        }
        return "redirect:/admin/managers";
    }

    @PostMapping("/managers/delete/{id}")
    public String deleteManager(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Удаление менеджера ID={}", id);
        try {
            adminService.deleteManager(id);
            logger.info("Менеджер ID={} успешно удален", id);
            redirectAttributes.addFlashAttribute("success", "Менеджер успешно удален");
        } catch (Exception e) {
            logger.error("Ошибка удаления менеджера ID={}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/managers";
    }

    // === СТАТИСТИКА И ОТЧЕТЫ ===
    @GetMapping("/statistics")
    public String statisticsPage(Model model) {
        logger.info("Запрос страницы статистики");
        try {
            // Общая статистика
            Map<String, Object> generalStats = adminService.getGeneralStatistics();
            model.addAllAttributes(generalStats);

            // Статистика по товарам для графика
            Map<String, Object> itemsStats = adminService.getItemsStatistics();

            // Преобразуем списки в строки для надежной передачи
            List<String> itemsLabels = (List<String>) itemsStats.get("labels");
            List<Integer> itemsData = (List<Integer>) itemsStats.get("data");

            model.addAttribute("itemsLabels", itemsLabels != null ? itemsLabels : Arrays.asList("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"));
            model.addAttribute("itemsData", itemsData != null ? itemsData : Arrays.asList(100, 120, 90, 150, 130, 110, 140));
            model.addAttribute("itemsTitle", itemsStats.get("title"));

            // Статистика по менеджерам
            Map<String, Object> managersStats = adminService.getManagersStatistics();
            List<String> managersLabels = (List<String>) managersStats.get("labels");
            List<Integer> managersData = (List<Integer>) managersStats.get("data");

            model.addAttribute("managersLabels", managersLabels != null ? managersLabels : Arrays.asList("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"));
            model.addAttribute("managersData", managersData != null ? managersData : Arrays.asList(2, 3, 3, 4, 4, 5, 5));
            model.addAttribute("managersTitle", managersStats.get("title"));

            // Статистика по заказам
            Map<String, Object> ordersStats = adminService.getOrdersStatistics();
            List<String> ordersLabels = (List<String>) ordersStats.get("labels");
            List<Integer> ordersData = (List<Integer>) ordersStats.get("data");

            model.addAttribute("ordersLabels", ordersLabels != null ? ordersLabels : Arrays.asList("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"));
            model.addAttribute("ordersData", ordersData != null ? ordersData : Arrays.asList(5, 8, 12, 6, 15, 3, 7));
            model.addAttribute("ordersTitle", ordersStats.get("title"));

            model.addAttribute("title", "Статистика магазина");
            logger.info("Страница статистики успешно загружена");
            return "admin/statistics";

        } catch (Exception e) {
            // Если ошибка - все равно показываем страницу с демо-данными
            logger.warn("Ошибка загрузки статистики, используются демо-данные: {}", e.getMessage());

            model.addAttribute("itemsLabels", Arrays.asList("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"));
            model.addAttribute("itemsData", Arrays.asList(100, 120, 90, 150, 130, 110, 140));
            model.addAttribute("itemsTitle", "Динамика товаров");

            model.addAttribute("managersLabels", Arrays.asList("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"));
            model.addAttribute("managersData", Arrays.asList(2, 3, 3, 4, 4, 5, 5));
            model.addAttribute("managersTitle", "Динамика менеджеров");

            model.addAttribute("ordersLabels", Arrays.asList("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"));
            model.addAttribute("ordersData", Arrays.asList(5, 8, 12, 6, 15, 3, 7));
            model.addAttribute("ordersTitle", "Динамика заказов");

            model.addAttribute("title", "Статистика магазина");
            return "admin/statistics";
        }
    }

    // === ЭКСПОРТ ДАННЫХ ===
    @GetMapping("/export/items")
    public void exportItems(HttpServletResponse response) throws IOException {
        logger.info("Запрос экспорта товаров");
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=items_export.xlsx");

            byte[] excelData = exportImportService.exportItemsToExcel();
            response.getOutputStream().write(excelData);
            response.getOutputStream().flush();
            logger.info("Экспорт товаров выполнен успешно");
        } catch (Exception e) {
            logger.error("Ошибка экспорта товаров: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/export/suppliers")
    public void exportSuppliers(HttpServletResponse response) throws IOException {
        logger.info("Запрос экспорта поставщиков");
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=suppliers_export.xlsx");

            byte[] excelData = exportImportService.exportSuppliersToExcel();
            response.getOutputStream().write(excelData);
            response.getOutputStream().flush();
            logger.info("Экспорт поставщиков выполнен успешно");
        } catch (Exception e) {
            logger.error("Ошибка экспорта поставщиков: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/export/categories")
    public void exportCategories(HttpServletResponse response) throws IOException {
        logger.info("Запрос экспорта категорий");
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=categories_export.xlsx");

            byte[] excelData = exportImportService.exportCategoriesToExcel();
            response.getOutputStream().write(excelData);
            response.getOutputStream().flush();
            logger.info("Экспорт категорий выполнен успешно");
        } catch (Exception e) {
            logger.error("Ошибка экспорта категорий: {}", e.getMessage(), e);
            throw e;
        }
    }

    // === ИМПОРТ ДАННЫХ ===
    @PostMapping("/import")
    public String importData(@RequestParam("file") MultipartFile file,
                             @RequestParam("dataType") String dataType,
                             RedirectAttributes redirectAttributes) {
        logger.info("Запрос импорта данных: тип={}, размер файла={}", dataType, file.getSize());
        try {
            if (file.isEmpty()) {
                logger.warn("Попытка импорта пустого файла");
                redirectAttributes.addFlashAttribute("error", "Файл не выбран");
                return "redirect:/admin/dashboard";
            }

            String result = exportImportService.importData(file, dataType);
            logger.info("Импорт данных выполнен успешно: {}", result);
            redirectAttributes.addFlashAttribute("success", result);

        } catch (Exception e) {
            logger.error("Ошибка импорта данных: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Ошибка импорта: " + e.getMessage());
        }

        return "redirect:/admin/dashboard";
    }

    // === ШАБЛОНЫ ДЛЯ ИМПОРТА ===
    @GetMapping("/templates")
    public String templatesPage(Model model) {
        logger.info("Запрос страницы шаблонов импорта");
        model.addAttribute("title", "Шаблоны для импорта");
        return "admin/templates";
    }

    // === API ДЛЯ ГРАФИКОВ (JSON endpoints) ===
    @GetMapping("/api/statistics/items")
    @ResponseBody
    public Map<String, Object> getItemsStatisticsApi() {
        logger.debug("API запрос статистики товаров");
        return adminService.getItemsStatistics();
    }

    @GetMapping("/api/statistics/managers")
    @ResponseBody
    public Map<String, Object> getManagersStatisticsApi() {
        logger.debug("API запрос статистики менеджеров");
        return adminService.getManagersStatistics();
    }

    @GetMapping("/api/statistics/orders")
    @ResponseBody
    public Map<String, Object> getOrdersStatisticsApi() {
        logger.debug("API запрос статистики заказов");
        return adminService.getOrdersStatistics();
    }

    @GetMapping("/api/statistics/general")
    @ResponseBody
    public Map<String, Object> getGeneralStatisticsApi() {
        logger.debug("API запрос общей статистики");
        return adminService.getGeneralStatistics();
    }

    // === ОТЧЕТЫ ===
    @GetMapping("/reports")
    public String reportsPage(Model model) {
        logger.info("Запрос страницы отчетов");
        model.addAttribute("title", "Отчеты магазина");

        // Устанавливаем даты по умолчанию для форм
        LocalDate defaultStartDate = LocalDate.now().minusMonths(1);
        LocalDate defaultEndDate = LocalDate.now();

        model.addAttribute("defaultStartDate", defaultStartDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("defaultEndDate", defaultEndDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        return "admin/reports";
    }

    @GetMapping("/reports/sales")
    public void generateSalesReport(@RequestParam(value = "startDate", required = false) String startDateStr,
                                    @RequestParam(value = "endDate", required = false) String endDateStr,
                                    HttpServletResponse response) throws IOException {
        logger.info("Генерация отчета по продажам: startDate={}, endDate={}", startDateStr, endDateStr);
        try {
            LocalDate startDate = startDateStr != null ? LocalDate.parse(startDateStr) : LocalDate.now().minusMonths(1);
            LocalDate endDate = endDateStr != null ? LocalDate.parse(endDateStr) : LocalDate.now();

            // Валидация дат
            if (startDate.isAfter(endDate)) {
                logger.warn("Некорректные даты для отчета по продажам: startDate={}, endDate={}", startDate, endDate);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Дата начала не может быть позже даты окончания");
                return;
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = "sales_report_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            byte[] reportData = reportService.generateSalesReport(startDate, endDate);
            response.getOutputStream().write(reportData);
            response.getOutputStream().flush();

            logger.info("Отчет по продажам сгенерирован успешно за период: {} - {}", startDate, endDate);

        } catch (Exception e) {
            logger.error("Ошибка генерации отчета по продажам: {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка генерации отчета: " + e.getMessage());
        }
    }

    @GetMapping("/reports/inventory")
    public void generateInventoryReport(HttpServletResponse response) throws IOException {
        logger.info("Генерация отчета по товарам");
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = "inventory_report_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            byte[] reportData = reportService.generateInventoryReport();
            response.getOutputStream().write(reportData);
            response.getOutputStream().flush();

            logger.info("Отчет по товарам сгенерирован успешно");

        } catch (Exception e) {
            logger.error("Ошибка генерации отчета по товарам: {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка генерации отчета: " + e.getMessage());
        }
    }

    @GetMapping("/reports/managers")
    public void generateManagersReport(@RequestParam(value = "startDate", required = false) String startDateStr,
                                       @RequestParam(value = "endDate", required = false) String endDateStr,
                                       HttpServletResponse response) throws IOException {
        logger.info("Генерация отчета по менеджерам: startDate={}, endDate={}", startDateStr, endDateStr);
        try {
            LocalDate startDate = startDateStr != null ? LocalDate.parse(startDateStr) : LocalDate.now().minusMonths(1);
            LocalDate endDate = endDateStr != null ? LocalDate.parse(endDateStr) : LocalDate.now();

            // Валидация дат
            if (startDate.isAfter(endDate)) {
                logger.warn("Некорректные даты для отчета по менеджерам: startDate={}, endDate={}", startDate, endDate);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Дата начала не может быть позже даты окончания");
                return;
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = "managers_report_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            byte[] reportData = reportService.generateManagersReport(startDate, endDate);
            response.getOutputStream().write(reportData);
            response.getOutputStream().flush();

            logger.info("Отчет по менеджерам сгенерирован успешно за период: {} - {}", startDate, endDate);

        } catch (Exception e) {
            logger.error("Ошибка генерации отчета по менеджерам: {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка генерации отчета: " + e.getMessage());
        }
    }

    @GetMapping("/reports/clients")
    public void generateClientsReport(@RequestParam(value = "startDate", required = false) String startDateStr,
                                      @RequestParam(value = "endDate", required = false) String endDateStr,
                                      HttpServletResponse response) throws IOException {
        logger.info("Генерация отчета по клиентам: startDate={}, endDate={}", startDateStr, endDateStr);
        try {
            LocalDate startDate = startDateStr != null ? LocalDate.parse(startDateStr) : LocalDate.now().minusMonths(1);
            LocalDate endDate = endDateStr != null ? LocalDate.parse(endDateStr) : LocalDate.now();

            // Валидация дат
            if (startDate.isAfter(endDate)) {
                logger.warn("Некорректные даты для отчета по клиентам: startDate={}, endDate={}", startDate, endDate);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Дата начала не может быть позже даты окончания");
                return;
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = "clients_report_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            byte[] reportData = reportService.generateClientsReport(startDate, endDate);
            response.getOutputStream().write(reportData);
            response.getOutputStream().flush();

            logger.info("Отчет по клиентам сгенерирован успешно за период: {} - {}", startDate, endDate);

        } catch (Exception e) {
            logger.error("Ошибка генерации отчета по клиентам: {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка генерации отчета: " + e.getMessage());
        }
    }

    // === БЫСТРЫЕ ОТЧЕТЫ (без параметров) ===
    @GetMapping("/reports/quick-sales")
    public void generateQuickSalesReport(HttpServletResponse response) throws IOException {
        logger.info("Генерация быстрого отчета по продажам");
        try {
            LocalDate startDate = LocalDate.now().minusMonths(1);
            LocalDate endDate = LocalDate.now();

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = "quick_sales_report_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            byte[] reportData = reportService.generateSalesReport(startDate, endDate);
            response.getOutputStream().write(reportData);
            response.getOutputStream().flush();

            logger.info("Быстрый отчет по продажам сгенерирован успешно");

        } catch (Exception e) {
            logger.error("Ошибка генерации быстрого отчета по продажам: {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка генерации отчета: " + e.getMessage());
        }
    }

    @GetMapping("/reports/quick-managers")
    public void generateQuickManagersReport(HttpServletResponse response) throws IOException {
        logger.info("Генерация быстрого отчета по менеджерам");
        try {
            LocalDate startDate = LocalDate.now().minusMonths(1);
            LocalDate endDate = LocalDate.now();

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = "quick_managers_report_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            byte[] reportData = reportService.generateManagersReport(startDate, endDate);
            response.getOutputStream().write(reportData);
            response.getOutputStream().flush();

            logger.info("Быстрый отчет по менеджерам сгенерирован успешно");

        } catch (Exception e) {
            logger.error("Ошибка генерации быстрого отчета по менеджерам: {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка генерации отчета: " + e.getMessage());
        }
    }

    // === ИНФОРМАЦИЯ О ДОСТУПНЫХ ОТЧЕТАХ ===
    @GetMapping("/api/reports/info")
    @ResponseBody
    public Map<String, Object> getReportsInfo() {
        logger.debug("Запрос информации об отчетах");
        Map<String, Object> info = new HashMap<>();

        try {
            long totalOrders = orderRepository.count();
            long totalItems = itemRepository.count();
            long totalManagers = userRepository.countByRole(Role.ROLE_MANAGER);
            long totalClients = clientRepository.count();

            info.put("totalOrders", totalOrders);
            info.put("totalItems", totalItems);
            info.put("totalManagers", totalManagers);
            info.put("totalClients", totalClients);
            info.put("availableReports", Arrays.asList(
                    "sales", "inventory", "managers", "clients"
            ));
            info.put("lastGenerated", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            info.put("success", true);

            logger.debug("Информация об отчетах загружена: заказы={}, товары={}, менеджеры={}, клиенты={}",
                    totalOrders, totalItems, totalManagers, totalClients);

        } catch (Exception e) {
            logger.error("Ошибка загрузки информации об отчетах: {}", e.getMessage(), e);
            info.put("success", false);
            info.put("error", e.getMessage());
        }

        return info;
    }
}