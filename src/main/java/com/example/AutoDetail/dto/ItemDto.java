package com.example.AutoDetail.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ItemDto {
    private Long id;
    private String arctical;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private String image;
    private Long supplierId;
    private String supplierName;

    // Конструкторы, геттеры и сеттеры
    public ItemDto() {}

    public ItemDto(Long id, String arctical, String name, BigDecimal price, Integer quantity,
                   String image, Long supplierId, String supplierName) {
        this.id = id;
        this.arctical = arctical;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getArctical() { return arctical; }
    public void setArctical(String arctical) { this.arctical = arctical; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
}