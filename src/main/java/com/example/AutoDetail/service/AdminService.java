package com.example.AutoDetail.service;

import com.example.AutoDetail.entity.*;
import com.example.AutoDetail.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final ItemRepository itemRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(ItemRepository itemRepository,
                        SupplierRepository supplierRepository,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder) {
        this.itemRepository = itemRepository;
        this.supplierRepository = supplierRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // === ТОВАРЫ (CRUD операции) ===
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    public List<Item> searchItemsByName(String name) {
        return itemRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Item> searchItemsByArctical(String arctical) {
        return itemRepository.findByArcticalContainingIgnoreCase(arctical);
    }

    public List<Item> filterItemsByPrice(Double minPrice, Double maxPrice) {
        return itemRepository.findByPriceRange(minPrice, maxPrice);
    }

    // === ПОСТАВЩИКИ (CRUD операции) ===
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Optional<Supplier> getSupplierById(Long id) {
        return supplierRepository.findById(id);
    }

    public Supplier saveSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    public void deleteSupplier(Long id) {
        supplierRepository.deleteById(id);
    }

    public List<Supplier> searchSuppliers(String searchTerm) {
        return supplierRepository.searchSuppliers(searchTerm);
    }

    public List<Supplier> searchSuppliersByName(String name) {
        return supplierRepository.findByNameContainingIgnoreCase(name);
    }

    // === МЕНЕДЖЕРЫ (CRUD операции) ===
    public List<User> getAllManagers() {
        return userRepository.findByRole(Role.ROLE_MANAGER);
    }

    public Optional<User> getManagerById(Long id) {
        return userRepository.findById(id);
    }

    public User saveManager(User manager) {
        // Шифруем пароль если он новый или изменен
        if (manager.getId() == null || manager.getPassword().startsWith("$2a$")) {
            // Пароль уже зашифрован
        } else {
            manager.setPassword(passwordEncoder.encode(manager.getPassword()));
        }
        manager.setRole(Role.ROLE_MANAGER);
        return userRepository.save(manager);
    }

    public void deleteManager(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> searchManagers(String searchTerm) {
        return userRepository.searchManagers(searchTerm);
    }

    public boolean isLoginExists(String login, Long excludeId) {
        Optional<User> existingUser = userRepository.findByLogin(login);
        if (existingUser.isPresent()) {
            return !existingUser.get().getId().equals(excludeId);
        }
        return false;
    }

    // === СТАТИСТИКА ===
    public long getTotalItems() {
        return itemRepository.count();
    }

    public long getTotalSuppliers() {
        return supplierRepository.count();
    }

    public long getTotalManagers() {
        return userRepository.countByRole(Role.ROLE_MANAGER);
    }

    public long getTotalClients() {
        // Здесь нужно добавить ClientRepository.count() когда будете его создавать
        return 0; // временно
    }
}
