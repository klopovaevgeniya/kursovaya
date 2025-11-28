package com.example.AutoDetail.controller;

import com.example.AutoDetail.dto.ClientDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@Tag(name = "Client API", description = "Операции с клиентами")
public class ClientApiController {

    @GetMapping
    @Operation(
            summary = "Получить всех клиентов",
            description = "Возвращает список всех клиентов с их данными"
    )
    public ResponseEntity<List<ClientDto>> getAllClients() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить клиента по ID",
            description = "Возвращает клиента по указанному идентификатору"
    )
    public ResponseEntity<ClientDto> getClientById(
            @Parameter(description = "ID клиента", example = "1", required = true)
            @PathVariable Long id) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search/phone/{phone}")
    @Operation(
            summary = "Найти клиента по телефону",
            description = "Возвращает клиента по номеру телефона"
    )
    public ResponseEntity<ClientDto> getClientByPhone(
            @Parameter(description = "Номер телефона", example = "+7-900-123-45-67", required = true)
            @PathVariable String phone) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search/login/{login}")
    @Operation(
            summary = "Найти клиента по логину",
            description = "Возвращает клиента по логину"
    )
    public ResponseEntity<ClientDto> getClientByLogin(
            @Parameter(description = "Логин клиента", example = "ivanov123", required = true)
            @PathVariable String login) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search/email/{email}")
    @Operation(
            summary = "Найти клиента по email",
            description = "Возвращает клиента по email адресу"
    )
    public ResponseEntity<ClientDto> getClientByEmail(
            @Parameter(description = "Email адрес", example = "ivanov@example.com", required = true)
            @PathVariable String email) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search/name/{name}")
    @Operation(
            summary = "Поиск клиентов по имени",
            description = "Возвращает клиентов с указанным именем"
    )
    public ResponseEntity<List<ClientDto>> getClientsByName(
            @Parameter(description = "Имя клиента", example = "Иван", required = true)
            @PathVariable String name) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/with-cars")
    @Operation(
            summary = "Получить клиентов с автомобилями",
            description = "Возвращает только тех клиентов, у которых есть автомобиль"
    )
    public ResponseEntity<List<ClientDto>> getClientsWithCars() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/without-cars")
    @Operation(
            summary = "Получить клиентов без автомобилей",
            description = "Возвращает только тех клиентов, у которых нет автомобиля"
    )
    public ResponseEntity<List<ClientDto>> getClientsWithoutCars() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/stats/count")
    @Operation(
            summary = "Получить статистику по клиентам",
            description = "Возвращает общее количество клиентов и другую статистику"
    )
    public ResponseEntity<Object> getClientsStats() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }
}