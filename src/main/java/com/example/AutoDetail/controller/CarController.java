package com.example.AutoDetail.controller;

import com.example.AutoDetail.dto.CarDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
@Tag(name = "Car API", description = "Операции с автомобилями")
public class CarController {

    // Временный список для демонстрации (замените на сервис)
    private List<CarDto> cars = Arrays.asList(
            new CarDto(1L, "Toyota", "Camry"),
            new CarDto(2L, "Honda", "Civic"),
            new CarDto(3L, "BMW", "X5")
    );

    @GetMapping
    @Operation(
            summary = "Получить все автомобили",
            description = "Возвращает список всех автомобилей"
    )
    public ResponseEntity<List<CarDto>> getAllCars() {
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить автомобиль по ID",
            description = "Возвращает автомобиль по указанному идентификатору"
    )
    public ResponseEntity<CarDto> getCarById(
            @Parameter(description = "ID автомобиля", example = "1", required = true)
            @PathVariable Long id) {

        CarDto car = cars.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (car != null) {
            return ResponseEntity.ok(car);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    @Operation(
            summary = "Поиск автомобилей по марке",
            description = "Возвращает автомобили по указанной марке"
    )
    public ResponseEntity<List<CarDto>> searchCarsByBrand(
            @Parameter(description = "Марка автомобиля", example = "Toyota", required = true)
            @RequestParam String brand) {

        List<CarDto> foundCars = cars.stream()
                .filter(car -> car.getCarBrand().equalsIgnoreCase(brand))
                .toList();

        return ResponseEntity.ok(foundCars);
    }

    @GetMapping("/brand/{brand}/model/{model}")
    @Operation(
            summary = "Поиск автомобиля по марке и модели",
            description = "Возвращает автомобиль по указанной марке и модели"
    )
    public ResponseEntity<CarDto> getCarByBrandAndModel(
            @Parameter(description = "Марка автомобиля", example = "Toyota")
            @PathVariable String brand,
            @Parameter(description = "Модель автомобиля", example = "Camry")
            @PathVariable String model) {

        CarDto car = cars.stream()
                .filter(c -> c.getCarBrand().equalsIgnoreCase(brand) &&
                        c.getCarModel().equalsIgnoreCase(model))
                .findFirst()
                .orElse(null);

        if (car != null) {
            return ResponseEntity.ok(car);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}