package com.example.AutoDetail.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Марка автомобиля обязательна")
    @Size(min = 2, max = 50, message = "Марка автомобиля должна быть от 2 до 50 символов")
    @Column(name = "car_brand")
    private String carBrand;

    @NotBlank(message = "Модель автомобиля обязательна")
    @Size(min = 1, max = 50, message = "Модель автомобиля должна быть от 1 до 50 символов")
    @Column(name = "car_model")
    private String carModel;

    // Конструкторы
    public Car() {}

    public Car(String carBrand, String carModel) {
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
}