package com.example.AutoDetail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO для деталей автомобиля")
public class CarDetailDto {

    @Schema(description = "ID деталей автомобиля", example = "1")
    private Long id;

    @NotBlank(message = "Тип топлива обязателен")
    @Size(max = 20, message = "Тип топлива не должен превышать 20 символов")
    @Schema(description = "Тип топлива", example = "Бензин")
    private String fuelType;

    @NotBlank(message = "Код двигателя обязателен")
    @Size(max = 20, message = "Код двигателя не должен превышать 20 символов")
    @Pattern(regexp = "^[A-Z0-9]{3,20}$", message = "Код двигателя должен содержать только заглавные буквы и цифры")
    @Schema(description = "Код двигателя", example = "2AZ123456")
    private String engineCode;

    @NotBlank(message = "VIN код обязателен")
    @Size(min = 17, max = 17, message = "VIN код должен содержать 17 символов")
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "Неверный формат VIN кода")
    @Schema(description = "VIN код", example = "JTDZN3EU4E3019501")
    private String vinCode;

    @NotNull(message = "Автомобиль обязателен")
    @Schema(description = "Автомобиль")
    private CarDto car;

    // Конструкторы
    public CarDetailDto() {}

    public CarDetailDto(Long id, String fuelType, String engineCode, String vinCode, CarDto car) {
        this.id = id;
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

    public CarDto getCar() { return car; }
    public void setCar(CarDto car) { this.car = car; }

    @Override
    public String toString() {
        return "CarDetailDTO{" +
                "id=" + id +
                ", fuelType='" + fuelType + '\'' +
                ", engineCode='" + engineCode + '\'' +
                ", vinCode='" + vinCode + '\'' +
                ", car=" + (car != null ? car.getCarBrand() + " " + car.getCarModel() : "null") +
                '}';
    }
}