package com.example.AutoDetail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO для автомобиля")
public class CarDto {

    @Schema(description = "ID автомобиля", example = "1")
    private Long id;

    @NotBlank(message = "Марка автомобиля обязательна")
    @Size(min = 2, max = 50, message = "Марка автомобиля должна быть от 2 до 50 символов")
    @Schema(description = "Марка автомобиля", example = "Toyota")
    private String carBrand;

    @NotBlank(message = "Модель автомобиля обязательна")
    @Size(min = 1, max = 50, message = "Модель автомобиля должна быть от 1 до 50 символов")
    @Schema(description = "Модель автомобиля", example = "Camry")
    private String carModel;

    // Конструкторы
    public CarDto() {}

    public CarDto(Long id, String carBrand, String carModel) {
        this.id = id;
        this.carBrand = carBrand;
        this.carModel = carModel;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCarBrand() { return carBrand; }
    public void setCarBrand(String carBrand) { this.carBrand = carBrand; }

    public String getCarModel() { return carModel; }
    public void setCarModel(String carModel) { this.carModel = carModel; }

    @Override
    public String toString() {
        return "CarDTO{" +
                "id=" + id +
                ", carBrand='" + carBrand + '\'' +
                ", carModel='" + carModel + '\'' +
                '}';
    }
}