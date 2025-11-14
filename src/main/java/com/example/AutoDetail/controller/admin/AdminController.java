package com.example.AutoDetail.controller.admin;

import com.example.AutoDetail.entity.*;
import com.example.AutoDetail.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
        try {
            long totalItems = adminService.getTotalItems();
            long totalSuppliers = adminService.getTotalSuppliers();
            long totalManagers = adminService.getTotalManagers();
            long totalClients = adminService.getTotalClients();
            long totalCategories = adminService.getTotalCategories();

            model.addAttribute("title", "Панель администратора");
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("totalSuppliers", totalSuppliers);
            model.addAttribute("totalManagers", totalManagers);
            model.addAttribute("totalClients", totalClients);
            model.addAttribute("totalCategories", totalCategories);

            return "admin/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки dashboard: " + e.getMessage());
            return "error";
        }
    }

    // === ТОВАРЫ ===
    @GetMapping("/items")
    public String itemsPage(@RequestParam(value = "search", required = false) String search,
                            Model model) {
        try {
            List<Item> items;
            if (search != null && !search.isEmpty()) {
                items = adminService.searchItems(search);
                model.addAttribute("searchType", "результаты поиска: " + search);
            } else {
                items = adminService.getAllItems();
                model.addAttribute("searchType", "все товары");
            }
            model.addAttribute("items", items);
            model.addAttribute("search", search);
            model.addAttribute("totalItems", items.size());
            return "admin/items";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки товаров: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/items/create")
    public String createItemForm(Model model) {
        try {
            List<Supplier> suppliers = adminService.getAllSuppliers();
            List<Category> categories = adminService.getAllCategories();
            model.addAttribute("item", new Item());
            model.addAttribute("suppliers", suppliers);
            model.addAttribute("categories", categories);
            model.addAttribute("isEdit", false);
            return "admin/item-form";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки формы: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/items")
    public String createItem(@Valid @ModelAttribute Item item,
                             BindingResult bindingResult,
                             @RequestParam("supplier.id") Long supplierId,
                             @RequestParam("category.id") Long categoryId,
                             @RequestParam(value = "imageUrl", required = false) String imageUrl,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                List<Supplier> suppliers = adminService.getAllSuppliers();
                List<Category> categories = adminService.getAllCategories();
                model.addAttribute("suppliers", suppliers);
                model.addAttribute("categories", categories);
                model.addAttribute("isEdit", false);
                return "admin/item-form";
            }

            adminService.saveItemWithImageUrl(item, supplierId, categoryId, imageUrl);
            redirectAttributes.addFlashAttribute("success", "Товар успешно создан");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/items";
    }

    @GetMapping("/items/edit/{id}")
    public String editItemForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Item> itemOpt = adminService.getItemById(id);
            if (itemOpt.isPresent()) {
                List<Supplier> suppliers = adminService.getAllSuppliers();
                List<Category> categories = adminService.getAllCategories();
                model.addAttribute("item", itemOpt.get());
                model.addAttribute("suppliers", suppliers);
                model.addAttribute("categories", categories);
                model.addAttribute("isEdit", true);
                return "admin/item-form";
            }
            redirectAttributes.addFlashAttribute("error", "Товар не найден");
            return "redirect:/admin/items";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки формы: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/items/update/{id}")
    public String updateItem(@PathVariable Long id,
                             @Valid @ModelAttribute Item item,
                             BindingResult bindingResult,
                             @RequestParam("supplier.id") Long supplierId,
                             @RequestParam("category.id") Long categoryId,
                             @RequestParam(value = "imageUrl", required = false) String imageUrl,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                List<Supplier> suppliers = adminService.getAllSuppliers();
                List<Category> categories = adminService.getAllCategories();
                model.addAttribute("suppliers", suppliers);
                model.addAttribute("categories", categories);
                model.addAttribute("isEdit", true);
                return "admin/item-form";
            }

            item.setId(id);
            adminService.saveItemWithImageUrl(item, supplierId, categoryId, imageUrl);
            redirectAttributes.addFlashAttribute("success", "Товар успешно обновлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/items";
    }

    @PostMapping("/items/delete/{id}")
    public String deleteItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteItem(id);
            redirectAttributes.addFlashAttribute("success", "Товар успешно удален");
            return "redirect:/admin/items";
        } catch (Exception e) {
            String cleanErrorMessage = e.getMessage().replaceAll("[\\r\\n]", " ").trim();
            redirectAttributes.addFlashAttribute("error", cleanErrorMessage);
            return "redirect:/admin/items";
        }
    }

    // === ПОСТАВЩИКИ ===
    @GetMapping("/suppliers")
    public String suppliersPage(@RequestParam(value = "search", required = false) String search,
                                Model model) {
        try {
            List<Supplier> suppliers;
            if (search != null && !search.isEmpty()) {
                suppliers = adminService.searchSuppliers(search);
                model.addAttribute("searchType", "результаты поиска: " + search);
            } else {
                suppliers = adminService.getAllSuppliers();
                model.addAttribute("searchType", "все поставщики");
            }
            model.addAttribute("suppliers", suppliers);
            model.addAttribute("search", search);
            model.addAttribute("totalSuppliers", suppliers.size());
            return "admin/suppliers";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки поставщиков: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/suppliers/create")
    public String createSupplierForm(Model model) {
        model.addAttribute("supplier", new Supplier());
        model.addAttribute("isEdit", false);
        return "admin/supplier-form";
    }

    @PostMapping("/suppliers")
    public String createSupplier(@Valid @ModelAttribute Supplier supplier,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                return "admin/supplier-form";
            }

            adminService.saveSupplier(supplier);
            redirectAttributes.addFlashAttribute("success", "Поставщик успешно создан");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/suppliers";
    }

    @GetMapping("/suppliers/edit/{id}")
    public String editSupplierForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Supplier> supplierOpt = adminService.getSupplierById(id);
            if (supplierOpt.isPresent()) {
                model.addAttribute("supplier", supplierOpt.get());
                model.addAttribute("isEdit", true);
                return "admin/supplier-form";
            }
            redirectAttributes.addFlashAttribute("error", "Поставщик не найден");
            return "redirect:/admin/suppliers";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки формы: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/suppliers/update/{id}")
    public String updateSupplier(@PathVariable Long id,
                                 @Valid @ModelAttribute Supplier supplier,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                return "admin/supplier-form";
            }

            supplier.setId(id);
            adminService.saveSupplier(supplier);
            redirectAttributes.addFlashAttribute("success", "Поставщик успешно обновлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/suppliers";
    }

    @PostMapping("/suppliers/delete/{id}")
    public String deleteSupplier(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteSupplier(id);
            redirectAttributes.addFlashAttribute("success", "Поставщик успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/suppliers";
    }

    // === КАТЕГОРИИ ===
    @GetMapping("/categories")
    public String categoriesPage(@RequestParam(value = "search", required = false) String search,
                                 Model model) {
        try {
            List<Category> categories;
            if (search != null && !search.isEmpty()) {
                categories = adminService.searchCategories(search);
                model.addAttribute("searchType", "результаты поиска: " + search);
            } else {
                categories = adminService.getAllCategories();
                model.addAttribute("searchType", "все категории");
            }
            model.addAttribute("categories", categories);
            model.addAttribute("search", search);
            model.addAttribute("totalCategories", categories.size());
            return "admin/categories";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки категорий: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/categories/create")
    public String createCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("isEdit", false);
        return "admin/category-form";
    }

    @PostMapping("/categories")
    public String createCategory(@Valid @ModelAttribute Category category,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                return "admin/category-form";
            }

            adminService.saveCategory(category);
            redirectAttributes.addFlashAttribute("success", "Категория успешно создана");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Category> categoryOpt = adminService.getCategoryById(id);
            if (categoryOpt.isPresent()) {
                model.addAttribute("category", categoryOpt.get());
                model.addAttribute("isEdit", true);
                return "admin/category-form";
            }
            redirectAttributes.addFlashAttribute("error", "Категория не найдена");
            return "redirect:/admin/categories";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки формы: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/categories/update/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @Valid @ModelAttribute Category category,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                return "admin/category-form";
            }

            category.setId(id);
            adminService.saveCategory(category);
            redirectAttributes.addFlashAttribute("success", "Категория успешно обновлена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("success", "Категория успешно удалена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    // === МЕНЕДЖЕРЫ ===
    @GetMapping("/managers")
    public String managersPage(@RequestParam(value = "search", required = false) String search,
                               Model model) {
        try {
            List<User> managers;
            if (search != null && !search.isEmpty()) {
                managers = adminService.searchManagers(search);
                model.addAttribute("searchType", "результаты поиска: " + search);
            } else {
                managers = adminService.getAllManagers();
                model.addAttribute("searchType", "все менеджеры");
            }
            model.addAttribute("managers", managers);
            model.addAttribute("search", search);
            model.addAttribute("totalManagers", managers.size());
            return "admin/managers";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки менеджеров: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/managers/create")
    public String createManagerForm(Model model) {
        model.addAttribute("manager", new User());
        model.addAttribute("isEdit", false);
        return "admin/manager-form";
    }

    @PostMapping("/managers")
    public String createManager(@Valid @ModelAttribute("manager") User manager,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                return "admin/manager-form";
            }

            if (adminService.isLoginExists(manager.getLogin(), null)) {
                redirectAttributes.addFlashAttribute("error", "Логин уже существует");
            } else {
                adminService.saveManager(manager);
                redirectAttributes.addFlashAttribute("success", "Менеджер успешно создан");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/managers";
    }

    @GetMapping("/managers/edit/{id}")
    public String editManagerForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<User> managerOpt = adminService.getManagerById(id);
            if (managerOpt.isPresent()) {
                model.addAttribute("manager", managerOpt.get());
                model.addAttribute("isEdit", true);
                return "admin/manager-form";
            }
            redirectAttributes.addFlashAttribute("error", "Менеджер не найден");
            return "redirect:/admin/managers";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки формы: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/managers/update/{id}")
    public String updateManager(@PathVariable Long id,
                                @Valid @ModelAttribute("manager") User manager,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                return "admin/manager-form";
            }

            if (adminService.isLoginExists(manager.getLogin(), id)) {
                redirectAttributes.addFlashAttribute("error", "Логин уже существует");
                return "redirect:/admin/managers/edit/" + id;
            }
            manager.setId(id);
            adminService.saveManager(manager);
            redirectAttributes.addFlashAttribute("success", "Менеджер успешно обновлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/managers";
    }

    @PostMapping("/managers/delete/{id}")
    public String deleteManager(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteManager(id);
            redirectAttributes.addFlashAttribute("success", "Менеджер успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/managers";
    }

    // === СТАТИСТИКА И ОТЧЕТЫ ===
    @GetMapping("/statistics")
    public String statisticsPage(Model model) {
        try {
            long totalItems = adminService.getTotalItems();
            long totalSuppliers = adminService.getTotalSuppliers();
            long totalManagers = adminService.getTotalManagers();
            long totalClients = adminService.getTotalClients();
            long totalCategories = adminService.getTotalCategories();

            model.addAttribute("title", "Статистика магазина");
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("totalSuppliers", totalSuppliers);
            model.addAttribute("totalManagers", totalManagers);
            model.addAttribute("totalClients", totalClients);
            model.addAttribute("totalCategories", totalCategories);

            return "admin/statistics";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки статистики: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/reports")
    public String reportsPage(Model model) {
        model.addAttribute("title", "Отчеты магазина");
        return "admin/reports";
    }
}