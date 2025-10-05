package com.example.AutoDetail.controller.admin;

import com.example.AutoDetail.entity.*;
import com.example.AutoDetail.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // === ГЛАВНАЯ ПАНЕЛЬ ===
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long totalItems = adminService.getTotalItems();
        long totalSuppliers = adminService.getTotalSuppliers();
        long totalManagers = adminService.getTotalManagers();
        long totalClients = adminService.getTotalClients();

        model.addAttribute("title", "Панель администратора");
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalSuppliers", totalSuppliers);
        model.addAttribute("totalManagers", totalManagers);
        model.addAttribute("totalClients", totalClients);

        return "admin/dashboard";
    }

    // === ТОВАРЫ (CRUD операции) ===
    @GetMapping("/items")
    public String itemsPage(@RequestParam(value = "search", required = false) String search,
                            @RequestParam(value = "arctical", required = false) String arctical,
                            @RequestParam(value = "minPrice", required = false) Double minPrice,
                            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
                            Model model) {
        List<Item> items;
        String searchType = "все товары";

        if (search != null && !search.isEmpty()) {
            items = adminService.searchItemsByName(search);
            searchType = "по названию: " + search;
        } else if (arctical != null && !arctical.isEmpty()) {
            items = adminService.searchItemsByArctical(arctical);
            searchType = "по артикулу: " + arctical;
        } else if (minPrice != null && maxPrice != null) {
            items = adminService.filterItemsByPrice(minPrice, maxPrice);
            searchType = "по цене: от " + minPrice + " до " + maxPrice;
        } else {
            items = adminService.getAllItems();
        }

        model.addAttribute("items", items);
        model.addAttribute("search", search);
        model.addAttribute("arctical", arctical);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("searchType", searchType);
        model.addAttribute("totalItems", items.size());

        return "admin/items";
    }

    // Форма создания товара
    @GetMapping("/items/create")
    public String createItemForm(Model model) {
        List<Supplier> suppliers = adminService.getAllSuppliers();
        model.addAttribute("item", new Item());
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("isEdit", false);
        return "admin/item-form";
    }

    // Создание товара
    @PostMapping("/items")
    public String createItem(@ModelAttribute Item item, RedirectAttributes redirectAttributes) {
        adminService.saveItem(item);
        redirectAttributes.addFlashAttribute("success", "Товар успешно создан");
        return "redirect:/admin/items";
    }

    // Форма редактирования товара
    @GetMapping("/items/edit/{id}")
    public String editItemForm(@PathVariable Long id, Model model) {
        Optional<Item> itemOpt = adminService.getItemById(id);
        if (itemOpt.isPresent()) {
            List<Supplier> suppliers = adminService.getAllSuppliers();
            model.addAttribute("item", itemOpt.get());
            model.addAttribute("suppliers", suppliers);
            model.addAttribute("isEdit", true);
            return "admin/item-form";
        }
        return "redirect:/admin/items?error=not_found";
    }

    // Обновление товара
    @PostMapping("/items/update/{id}")
    public String updateItem(@PathVariable Long id, @ModelAttribute Item item, RedirectAttributes redirectAttributes) {
        item.setId(id);
        adminService.saveItem(item);
        redirectAttributes.addFlashAttribute("success", "Товар успешно обновлен");
        return "redirect:/admin/items";
    }

    // Удаление товара
    @PostMapping("/items/delete/{id}")
    public String deleteItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminService.deleteItem(id);
        redirectAttributes.addFlashAttribute("success", "Товар успешно удален");
        return "redirect:/admin/items";
    }

    // === ПОСТАВЩИКИ (CRUD операции) ===
    @GetMapping("/suppliers")
    public String suppliersPage(@RequestParam(value = "search", required = false) String search,
                                Model model) {
        List<Supplier> suppliers;
        String searchType = "все поставщики";

        if (search != null && !search.isEmpty()) {
            suppliers = adminService.searchSuppliers(search);
            searchType = "результаты поиска: " + search;
        } else {
            suppliers = adminService.getAllSuppliers();
        }

        model.addAttribute("suppliers", suppliers);
        model.addAttribute("search", search);
        model.addAttribute("searchType", searchType);
        model.addAttribute("totalSuppliers", suppliers.size());

        return "admin/suppliers";
    }

    // Форма создания поставщика
    @GetMapping("/suppliers/create")
    public String createSupplierForm(Model model) {
        model.addAttribute("supplier", new Supplier());
        model.addAttribute("isEdit", false);
        return "admin/supplier-form";
    }

    // Создание поставщика
    @PostMapping("/suppliers")
    public String createSupplier(@ModelAttribute Supplier supplier, RedirectAttributes redirectAttributes) {
        adminService.saveSupplier(supplier);
        redirectAttributes.addFlashAttribute("success", "Поставщик успешно создан");
        return "redirect:/admin/suppliers";
    }

    // Форма редактирования поставщика
    @GetMapping("/suppliers/edit/{id}")
    public String editSupplierForm(@PathVariable Long id, Model model) {
        Optional<Supplier> supplierOpt = adminService.getSupplierById(id);
        if (supplierOpt.isPresent()) {
            model.addAttribute("supplier", supplierOpt.get());
            model.addAttribute("isEdit", true);
            return "admin/supplier-form";
        }
        return "redirect:/admin/suppliers?error=not_found";
    }

    // Обновление поставщика
    @PostMapping("/suppliers/update/{id}")
    public String updateSupplier(@PathVariable Long id, @ModelAttribute Supplier supplier, RedirectAttributes redirectAttributes) {
        supplier.setId(id);
        adminService.saveSupplier(supplier);
        redirectAttributes.addFlashAttribute("success", "Поставщик успешно обновлен");
        return "redirect:/admin/suppliers";
    }

    // Удаление поставщика
    @PostMapping("/suppliers/delete/{id}")
    public String deleteSupplier(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminService.deleteSupplier(id);
        redirectAttributes.addFlashAttribute("success", "Поставщик успешно удален");
        return "redirect:/admin/suppliers";
    }

    // === МЕНЕДЖЕРЫ (CRUD операции) ===
    @GetMapping("/managers")
    public String managersPage(@RequestParam(value = "search", required = false) String search,
                               Model model) {
        List<User> managers;
        String searchType = "все менеджеры";

        if (search != null && !search.isEmpty()) {
            managers = adminService.searchManagers(search);
            searchType = "результаты поиска: " + search;
        } else {
            managers = adminService.getAllManagers();
        }

        model.addAttribute("managers", managers);
        model.addAttribute("search", search);
        model.addAttribute("searchType", searchType);
        model.addAttribute("totalManagers", managers.size());

        return "admin/managers";
    }

    // Форма создания менеджера
    @GetMapping("/managers/create")
    public String createManagerForm(Model model) {
        model.addAttribute("manager", new User());
        model.addAttribute("isEdit", false);
        return "admin/manager-form";
    }

    // Создание менеджера
    @PostMapping("/managers")
    public String createManager(@ModelAttribute User manager,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (adminService.isLoginExists(manager.getLogin(), null)) {
            model.addAttribute("error", "Логин уже существует");
            model.addAttribute("manager", manager);
            model.addAttribute("isEdit", false);
            return "admin/manager-form";
        }

        // Проверка пароля для нового менеджера
        if (manager.getPassword() == null || manager.getPassword().trim().isEmpty()) {
            model.addAttribute("error", "Пароль обязателен для нового менеджера");
            model.addAttribute("manager", manager);
            model.addAttribute("isEdit", false);
            return "admin/manager-form";
        }

        adminService.saveManager(manager);
        redirectAttributes.addFlashAttribute("success", "Менеджер успешно создан");
        return "redirect:/admin/managers";
    }

    // Форма редактирования менеджера
    @GetMapping("/managers/edit/{id}")
    public String editManagerForm(@PathVariable Long id, Model model) {
        Optional<User> managerOpt = adminService.getManagerById(id);
        if (managerOpt.isPresent()) {
            model.addAttribute("manager", managerOpt.get());
            model.addAttribute("isEdit", true);
            return "admin/manager-form";
        }
        return "redirect:/admin/managers?error=not_found";
    }

    // Обновление менеджера
    @PostMapping("/managers/update/{id}")
    public String updateManager(@PathVariable Long id,
                                @ModelAttribute User manager,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (adminService.isLoginExists(manager.getLogin(), id)) {
            model.addAttribute("error", "Логин уже существует");
            model.addAttribute("manager", manager);
            model.addAttribute("isEdit", true);
            return "admin/manager-form";
        }

        manager.setId(id);
        adminService.saveManager(manager);
        redirectAttributes.addFlashAttribute("success", "Менеджер успешно обновлен");
        return "redirect:/admin/managers";
    }

    // Удаление менеджера
    @PostMapping("/managers/delete/{id}")
    public String deleteManager(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminService.deleteManager(id);
        redirectAttributes.addFlashAttribute("success", "Менеджер успешно удален");
        return "redirect:/admin/managers";
    }

    // === СТАТИСТИКА ===
    @GetMapping("/statistics")
    public String statisticsPage(Model model) {
        long totalItems = adminService.getTotalItems();
        long totalSuppliers = adminService.getTotalSuppliers();
        long totalManagers = adminService.getTotalManagers();
        long totalClients = adminService.getTotalClients();

        model.addAttribute("title", "Статистика магазина");
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalSuppliers", totalSuppliers);
        model.addAttribute("totalManagers", totalManagers);
        model.addAttribute("totalClients", totalClients);

        return "admin/statistics";
    }

    // === ОТЧЕТЫ ===
    @GetMapping("/reports")
    public String reportsPage(Model model) {
        model.addAttribute("title", "Отчеты магазина");
        return "admin/reports";
    }

    // === ДИАГНОСТИКА ===
    @GetMapping("/test-urls")
    @ResponseBody
    public String testUrls() {
        return "Доступные URL админа:\n" +
                "GET  /admin/dashboard - главная панель\n" +
                "GET  /admin/items - список товаров\n" +
                "GET  /admin/items/create - форма создания товара\n" +
                "POST /admin/items - создание товара\n" +
                "GET  /admin/items/edit/{id} - форма редактирования товара\n" +
                "POST /admin/items/update/{id} - обновление товара\n" +
                "POST /admin/items/delete/{id} - удаление товара\n" +
                "GET  /admin/suppliers - список поставщиков\n" +
                "GET  /admin/suppliers/create - форма создания поставщика\n" +
                "POST /admin/suppliers - создание поставщика\n" +
                "GET  /admin/suppliers/edit/{id} - форма редактирования поставщика\n" +
                "POST /admin/suppliers/update/{id} - обновление поставщика\n" +
                "POST /admin/suppliers/delete/{id} - удаление поставщика\n" +
                "GET  /admin/managers - список менеджеров\n" +
                "GET  /admin/managers/create - форма создания менеджера\n" +
                "POST /admin/managers - создание менеджера\n" +
                "GET  /admin/managers/edit/{id} - форма редактирования менеджера\n" +
                "POST /admin/managers/update/{id} - обновление менеджера\n" +
                "POST /admin/managers/delete/{id} - удаление менеджера\n" +
                "GET  /admin/statistics - статистика\n" +
                "GET  /admin/reports - отчеты";
    }
}