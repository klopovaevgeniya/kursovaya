package com.example.AutoDetail.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    private String arctical;
    private String name;
    private Double price;
    private Integer quantity;
    private String image;

    // Конструкторы
    public Item() {}

    public Item(String arctical, String name, Double price, Integer quantity, String image, Supplier supplier) {
        this.arctical = arctical;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
        this.supplier = supplier;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public String getArctical() { return arctical; }
    public void setArctical(String arctical) { this.arctical = arctical; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}