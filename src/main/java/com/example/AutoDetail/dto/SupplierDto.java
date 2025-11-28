package com.example.AutoDetail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO для поставщика")
public class SupplierDto {

    @Schema(description = "ID поставщика", example = "1")
    private Long id;

    @NotBlank(message = "Название компании обязательно")
    @Size(min = 2, max = 255, message = "Название должно быть от 2 до 255 символов")
    @Schema(description = "Название компании", example = "AutoParts Ltd.")
    private String name;

    @NotBlank(message = "Контактный телефон обязателен")
    @Pattern(regexp = "^[\\+]?[0-9\\-\\(\\)\\s]{7,15}$", message = "Неверный формат телефона")
    @Schema(description = "Контактный телефон", example = "+7-800-123-45-67")
    private String contactPhone;

    @Email(message = "Неверный формат email")
    @Schema(description = "Контактный email", example = "info@autoparts.ru")
    private String contactEmail;

    @Schema(description = "Количество товаров от поставщика", example = "25")
    private Integer itemsCount;

    // Конструкторы
    public SupplierDto() {}

    public SupplierDto(Long id, String name, String contactPhone, String contactEmail) {
        this.id = id;
        this.name = name;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
    }

    public SupplierDto(Long id, String name, String contactPhone, String contactEmail, Integer itemsCount) {
        this.id = id;
        this.name = name;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.itemsCount = itemsCount;
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

    public Integer getItemsCount() { return itemsCount; }
    public void setItemsCount(Integer itemsCount) { this.itemsCount = itemsCount; }

    @Override
    public String toString() {
        return "SupplierDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", itemsCount=" + itemsCount +
                '}';
    }
}