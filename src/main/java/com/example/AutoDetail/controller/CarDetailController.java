package com.example.AutoDetail.controller;

import com.example.AutoDetail.dto.CarDetailDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/car-details")
@Tag(name = "Car Detail API", description = "Операции с деталями автомобилей")
public class CarDetailController {

    @GetMapping
    @Operation(
            summary = "Получить все детали автомобилей",
            description = "Возвращает список всех деталей автомобилей"
    )
    public ResponseEntity<List<CarDetailDto>> getAllCarDetails() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить детали автомобиля по ID",
            description = "Возвращает детали автомобиля по указанному идентификатору"
    )
    public ResponseEntity<CarDetailDto> getCarDetailById(
            @Parameter(description = "ID деталей автомобиля", example = "1", required = true)
            @PathVariable Long id) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/vin/{vinCode}")
    @Operation(
            summary = "Найти детали автомобиля по VIN коду",
            description = "Возвращает детали автомобиля по VIN коду"
    )
    public ResponseEntity<CarDetailDto> getCarDetailByVin(
            @Parameter(description = "VIN код автомобиля", example = "JTDZN3EU4E3019501", required = true)
            @PathVariable String vinCode) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/engine/{engineCode}")
    @Operation(
            summary = "Найти детали автомобиля по коду двигателя",
            description = "Возвращает детали автомобиля по коду двигателя"
    )
    public ResponseEntity<CarDetailDto> getCarDetailByEngineCode(
            @Parameter(description = "Код двигателя", example = "2AZ123456", required = true)
            @PathVariable String engineCode) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/fuel-type/{fuelType}")
    @Operation(
            summary = "Найти автомобили по типу топлива",
            description = "Возвращает список автомобилей с указанным типом топлива"
    )
    public ResponseEntity<List<CarDetailDto>> getCarDetailsByFuelType(
            @Parameter(description = "Тип топлива", example = "Бензин", required = true)
            @PathVariable String fuelType) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/car/{carId}")
    @Operation(
            summary = "Найти детали по ID автомобиля",
            description = "Возвращает детали для указанного автомобиля"
    )
    public ResponseEntity<CarDetailDto> getCarDetailByCarId(
            @Parameter(description = "ID автомобиля", example = "1", required = true)
            @PathVariable Long carId) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }
}