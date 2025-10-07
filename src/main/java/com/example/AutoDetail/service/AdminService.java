package com.example.AutoDetail.service;

import com.example.AutoDetail.entity.*;
import com.example.AutoDetail.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
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

    // === ТОВАРЫ ===
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    public Item saveItem(Item item, Long supplierId) {
        // Находим поставщика
        Optional<Supplier> supplierOpt = supplierRepository.findById(supplierId);
        if (supplierOpt.isPresent()) {
            item.setSupplier(supplierOpt.get());
        } else {
            throw new RuntimeException("Поставщик не найден");
        }
        return itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    public List<Item> searchItemsByName(String name) {
        return itemRepository.findByNameContainingIgnoreCase(name);
    }

    // === ПОСТАВЩИКИ ===
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

    // === МЕНЕДЖЕРЫ ===
    public List<User> getAllManagers() {
        return userRepository.findByRole(Role.ROLE_MANAGER);
    }

    public Optional<User> getManagerById(Long id) {
        return userRepository.findById(id);
    }

    public User saveManager(User manager) {
        // Шифруем пароль
        if (manager.getPassword() != null && !manager.getPassword().startsWith("$2a$")) {
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
            if (excludeId == null) {
                return true;
            }
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
        return userRepository.countByRole(Role.ROLE_CLIENT);
    }
}