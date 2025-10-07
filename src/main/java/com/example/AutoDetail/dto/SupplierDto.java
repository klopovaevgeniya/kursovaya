package com.example.AutoDetail.dto;

public class SupplierDto {
    private Long id;
    private String name;
    private String contactPhone;
    private String contactEmail;

    // Конструкторы, геттеры и сеттеры
    public SupplierDto() {}

    public SupplierDto(Long id, String name, String contactPhone, String contactEmail) {
        this.id = id;
        this.name = name;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
}