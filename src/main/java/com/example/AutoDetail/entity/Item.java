package com.example.AutoDetail.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    @NotNull(message = "Поставщик обязателен")
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @NotNull(message = "Категория обязательна")
    private Category category;

    @NotBlank(message = "Артикул обязателен")
    @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "Артикул может содержать только буквы, цифры и дефисы")
    @Column(unique = true, updatable = false)
    private String arctical;

    @NotBlank(message = "Название товара обязательно")
    @Size(min = 2, max = 255, message = "Название должно быть от 2 до 255 символов")
    private String name;

    @NotNull(message = "Цена обязательна")
    @DecimalMin(value = "0.0", inclusive = false, message = "Цена должна быть больше 0")
    private Double price;

    @NotNull(message = "Количество обязательно")
    @Min(value = 0, message = "Количество не может быть отрицательным")
    private Integer quantity;

    private String image;

    @Column(name = "is_articul_generated")
    private Boolean isArticulGenerated = false;

    // ДОБАВЛЕНО ПОЛЕ ДОСТУПНОСТИ
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    // Конструкторы
    public Item() {
        this.isArticulGenerated = false;
        this.isAvailable = true;
    }

    public Item(String arctical, String name, Double price, Integer quantity, String image, Supplier supplier, Category category) {
        this.arctical = arctical;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
        this.supplier = supplier;
        this.category = category;
        this.isArticulGenerated = false;
        this.isAvailable = true;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getArctical() { return arctical; }
    public void setArctical(String arctical) {
        if (Boolean.TRUE.equals(this.isArticulGenerated) && this.arctical != null) {
            return;
        }
        this.arctical = arctical;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Boolean getIsArticulGenerated() {
        return isArticulGenerated != null ? isArticulGenerated : false;
    }

    public void setIsArticulGenerated(Boolean isArticulGenerated) {
        this.isArticulGenerated = isArticulGenerated != null ? isArticulGenerated : false;
    }

    // ДОБАВЛЕНЫ ГЕТТЕРЫ И СЕТТЕРЫ ДЛЯ isAvailable
    public Boolean getIsAvailable() {
        return isAvailable != null ? isAvailable : true;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable != null ? isAvailable : true;
    }

    // Альтернативный геттер для совместимости
    public Boolean isAvailable() {
        return getIsAvailable();
    }
}