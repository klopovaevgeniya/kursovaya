package com.example.AutoDetail.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "suppliers")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String contactPhone;
    private String contactEmail;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Item> items = new ArrayList<>();

    // Конструкторы
    public Supplier() {}

    public Supplier(String name, String contactPhone, String contactEmail) {
        this.name = name;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }

    // Вспомогательные методы
    public void addItem(Item item) {
        items.add(item);
        item.setSupplier(this);
    }

    public void removeItem(Item item) {
        items.remove(item);
        item.setSupplier(null);
    }
}