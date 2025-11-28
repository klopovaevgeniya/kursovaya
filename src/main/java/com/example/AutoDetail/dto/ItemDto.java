package com.example.AutoDetail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO для товара")
public class ItemDto {

    @Schema(description = "ID товара", example = "1")
    private Long id;

    @NotNull(message = "Поставщик обязателен")
    @Schema(description = "Поставщик товара")
    private SupplierDto supplier;

    @NotNull(message = "Категория обязательна")
    @Schema(description = "Категория товара")
    private CategoryDto category;

    @NotBlank(message = "Артикул обязателен")
    @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "Артикул может содержать только буквы, цифры и дефисы")
    @Schema(description = "Артикул товара", example = "ABC-12345")
    private String arctical;

    @NotBlank(message = "Название товара обязательно")
    @Size(min = 2, max = 255, message = "Название должно быть от 2 до 255 символов")
    @Schema(description = "Название товара", example = "Масло моторное 5W-30")
    private String name;

    @NotNull(message = "Цена обязательна")
    @DecimalMin(value = "0.0", inclusive = false, message = "Цена должна быть больше 0")
    @Schema(description = "Цена товара", example = "2500.50")
    private Double price;

    @NotNull(message = "Количество обязательно")
    @Min(value = 0, message = "Количество не может быть отрицательным")
    @Schema(description = "Количество на складе", example = "100")
    private Integer quantity;

    @Schema(description = "URL изображения товара", example = "/images/oil-5w30.jpg")
    private String image;

    @Schema(description = "Сгенерирован ли артикул автоматически", example = "false")
    private Boolean isArticulGenerated = false;

    // Конструкторы
    public ItemDto() {
        this.isArticulGenerated = false;
    }

    public ItemDto(Long id, SupplierDto supplier, CategoryDto category, String arctical,
                   String name, Double price, Integer quantity, String image, Boolean isArticulGenerated) {
        this.id = id;
        this.supplier = supplier;
        this.category = category;
        this.arctical = arctical;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
        this.isArticulGenerated = isArticulGenerated != null ? isArticulGenerated : false;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public SupplierDto getSupplier() { return supplier; }
    public void setSupplier(SupplierDto supplier) { this.supplier = supplier; }

    public CategoryDto getCategory() { return category; }
    public void setCategory(CategoryDto category) { this.category = category; }

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

    public Boolean getIsArticulGenerated() {
        return isArticulGenerated != null ? isArticulGenerated : false;
    }
    public void setIsArticulGenerated(Boolean isArticulGenerated) {
        this.isArticulGenerated = isArticulGenerated != null ? isArticulGenerated : false;
    }

    // Дополнительные методы для удобства
    @Schema(description = "Общая стоимость товара на складе", example = "250050.0")
    public Double getTotalValue() {
        return price * quantity;
    }

    @Schema(description = "Есть ли товар в наличии", example = "true")
    public Boolean isInStock() {
        return quantity != null && quantity > 0;
    }

    @Schema(description = "Количество товара в наличии текстом", example = "В наличии")
    public String getStockStatus() {
        if (quantity == null) return "Неизвестно";
        if (quantity == 0) return "Нет в наличии";
        if (quantity < 10) return "Мало";
        return "В наличии";
    }

    @Override
    public String toString() {
        return "ItemDTO{" +
                "id=" + id +
                ", arctical='" + arctical + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", inStock=" + isInStock() +
                '}';
    }
}