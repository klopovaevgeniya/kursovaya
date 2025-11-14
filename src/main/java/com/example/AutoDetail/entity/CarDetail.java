package com.example.AutoDetail.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "cart_details")
public class CarDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Тип топлива обязателен")
    @Size(max = 20, message = "Тип топлива не должен превышать 20 символов")
    @Column(name = "fuel_type")
    private String fuelType;

    @NotBlank(message = "Код двигателя обязателен")
    @Size(max = 20, message = "Код двигателя не должен превышать 20 символов")
    @Pattern(regexp = "^[A-Z0-9]{3,20}$", message = "Код двигателя должен содержать только заглавные буквы и цифры")
    @Column(name = "engine_code")
    private String engineCode;

    @NotBlank(message = "VIN код обязателен")
    @Size(min = 17, max = 17, message = "VIN код должен содержать 17 символов")
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "Неверный формат VIN кода")
    @Column(name = "vin_code", unique = true)
    private String vinCode;

    @NotNull(message = "Автомобиль обязателен")
    @ManyToOne
    @JoinColumn(name = "fk_id_car", nullable = false)
    private Car car;

    // Конструкторы
    public CarDetail() {}

    public CarDetail(String fuelType, String engineCode, String vinCode, Car car) {
        this.fuelType = fuelType;
        this.engineCode = engineCode;
        this.vinCode = vinCode;
        this.car = car;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public String getEngineCode() { return engineCode; }
    public void setEngineCode(String engineCode) { this.engineCode = engineCode; }

    public String getVinCode() { return vinCode; }
    public void setVinCode(String vinCode) { this.vinCode = vinCode; }

    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }
}