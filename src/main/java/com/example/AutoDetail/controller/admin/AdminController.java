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

    // === ТОВАРЫ ===
    @GetMapping("/items")
    public String itemsPage(@RequestParam(value = "search", required = false) String search,
                            Model model) {
        List<Item> items;
        if (search != null && !search.isEmpty()) {
            items = adminService.searchItemsByName(search);
        } else {
            items = adminService.getAllItems();
        }
        model.addAttribute("items", items);
        return "admin/items";
    }

    @GetMapping("/items/create")
    public String createItemForm(Model model) {
        List<Supplier> suppliers = adminService.getAllSuppliers();
        model.addAttribute("item", new Item());
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("isEdit", false);
        return "admin/item-form";
    }

    @PostMapping("/items")
    public String createItem(@ModelAttribute Item item,
                             @RequestParam Long supplierId,
                             RedirectAttributes redirectAttributes) {
        try {
            adminService.saveItem(item, supplierId);
            redirectAttributes.addFlashAttribute("success", "Товар успешно создан");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }
        return "redirect:/admin/items";
    }

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

    @PostMapping("/items/update/{id}")
    public String updateItem(@PathVariable Long id,
                             @ModelAttribute Item item,
                             @RequestParam Long supplierId,
                             RedirectAttributes redirectAttributes) {
        try {
            item.setId(id);
            adminService.saveItem(item, supplierId);
            redirectAttributes.addFlashAttribute("success", "Товар успешно обновлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }
        return "redirect:/admin/items";
    }

    @PostMapping("/items/delete/{id}")
    public String deleteItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteItem(id);
            redirectAttributes.addFlashAttribute("success", "Товар успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }
        return "redirect:/admin/items";
    }

    // === ПОСТАВЩИКИ ===
    @GetMapping("/suppliers")
    public String suppliersPage(Model model) {
        List<Supplier> suppliers = adminService.getAllSuppliers();
        model.addAttribute("suppliers", suppliers);
        return "admin/suppliers";
    }

    @GetMapping("/suppliers/create")
    public String createSupplierForm(Model model) {
        model.addAttribute("supplier", new Supplier());
        model.addAttribute("isEdit", false);
        return "admin/supplier-form";
    }

    @PostMapping("/suppliers")
    public String createSupplier(@ModelAttribute Supplier supplier, RedirectAttributes redirectAttributes) {
        try {
            adminService.saveSupplier(supplier);
            redirectAttributes.addFlashAttribute("success", "Поставщик успешно создан");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }
        return "redirect:/admin/suppliers";
    }

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

    @PostMapping("/suppliers/update/{id}")
    public String updateSupplier(@PathVariable Long id, @ModelAttribute Supplier supplier, RedirectAttributes redirectAttributes) {
        try {
            supplier.setId(id);
            adminService.saveSupplier(supplier);
            redirectAttributes.addFlashAttribute("success", "Поставщик успешно обновлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }
        return "redirect:/admin/suppliers";
    }

    @PostMapping("/suppliers/delete/{id}")
    public String deleteSupplier(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteSupplier(id);
            redirectAttributes.addFlashAttribute("success", "Поставщик успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }
        return "redirect:/admin/suppliers";
    }

    // === МЕНЕДЖЕРЫ ===
    @GetMapping("/managers")
    public String managersPage(Model model) {
        List<User> managers = adminService.getAllManagers();
        model.addAttribute("managers", managers);
        return "admin/managers";
    }

    @GetMapping("/managers/create")
    public String createManagerForm(Model model) {
        model.addAttribute("manager", new User());
        model.addAttribute("isEdit", false);
        return "admin/manager-form";
    }

    @PostMapping("/managers")
    public String createManager(@ModelAttribute User manager, RedirectAttributes redirectAttributes) {
        try {
            if (adminService.isLoginExists(manager.getLogin(), null)) {
                redirectAttributes.addFlashAttribute("error", "Логин уже существует");
                return "redirect:/admin/managers/create";
            }
            adminService.saveManager(manager);
            redirectAttributes.addFlashAttribute("success", "Менеджер успешно создан");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }
        return "redirect:/admin/managers";
    }

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

    @PostMapping("/managers/update/{id}")
    public String updateManager(@PathVariable Long id, @ModelAttribute User manager, RedirectAttributes redirectAttributes) {
        try {
            if (adminService.isLoginExists(manager.getLogin(), id)) {
                redirectAttributes.addFlashAttribute("error", "Логин уже существует");
                return "redirect:/admin/managers/edit/" + id;
            }
            manager.setId(id);
            adminService.saveManager(manager);
            redirectAttributes.addFlashAttribute("success", "Менеджер успешно обновлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }
        return "redirect:/admin/managers";
    }

    @PostMapping("/managers/delete/{id}")
    public String deleteManager(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteManager(id);
            redirectAttributes.addFlashAttribute("success", "Менеджер успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }
        return "redirect:/admin/managers";
    }

    // === СТАТИСТИКА И ОТЧЕТЫ ===
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

    @GetMapping("/reports")
    public String reportsPage(Model model) {
        model.addAttribute("title", "Отчеты магазина");
        return "admin/reports";
    }
}