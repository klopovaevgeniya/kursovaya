package com.example.AutoDetail.controller;

import com.example.AutoDetail.dto.UserDto;
import com.example.AutoDetail.entity.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User API", description = "Операции с пользователями системы")
public class UserController {

    @GetMapping
    @Operation(
            summary = "Получить всех пользователей",
            description = "Возвращает список всех пользователей системы"
    )
    public ResponseEntity<List<UserDto>> getAllUsers() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить пользователя по ID",
            description = "Возвращает пользователя по указанному идентификатору"
    )
    public ResponseEntity<UserDto> getUserById(
            @Parameter(description = "ID пользователя", example = "1", required = true)
            @PathVariable Long id) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search/login/{login}")
    @Operation(
            summary = "Найти пользователя по логину",
            description = "Возвращает пользователя по логину"
    )
    public ResponseEntity<UserDto> getUserByLogin(
            @Parameter(description = "Логин пользователя", example = "ivanov_admin", required = true)
            @PathVariable String login) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search/name/{name}")
    @Operation(
            summary = "Поиск пользователей по имени",
            description = "Возвращает пользователей с указанным именем"
    )
    public ResponseEntity<List<UserDto>> getUsersByName(
            @Parameter(description = "Имя пользователя", example = "Иван", required = true)
            @PathVariable String name) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/role/{role}")
    @Operation(
            summary = "Получить пользователей по роли",
            description = "Возвращает всех пользователей с указанной ролью"
    )
    public ResponseEntity<List<UserDto>> getUsersByRole(
            @Parameter(description = "Роль пользователя", example = "ADMIN", required = true)
            @PathVariable Role role) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/search/phone/{phone}")
    @Operation(
            summary = "Найти пользователя по телефону",
            description = "Возвращает пользователя по номеру телефона"
    )
    public ResponseEntity<UserDto> getUserByPhone(
            @Parameter(description = "Номер телефона", example = "+7-900-123-45-67", required = true)
            @PathVariable String phone) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/active")
    @Operation(
            summary = "Получить активных пользователей",
            description = "Возвращает список активных пользователей системы"
    )
    public ResponseEntity<List<UserDto>> getActiveUsers() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}/full-info")
    @Operation(
            summary = "Получить полную информацию о пользователе",
            description = "Возвращает расширенную информацию о пользователе"
    )
    public ResponseEntity<UserDto> getUserFullInfo(
            @Parameter(description = "ID пользователя", example = "1", required = true)
            @PathVariable Long id) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats/count")
    @Operation(
            summary = "Получить статистику по пользователям",
            description = "Возвращает общее количество пользователей и статистику по ролям"
    )
    public ResponseEntity<Object> getUsersStats() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats/count-by-role")
    @Operation(
            summary = "Получить количество пользователей по ролям",
            description = "Возвращает статистику по количеству пользователей в каждой роли"
    )
    public ResponseEntity<Object> getUsersCountByRole() {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/profile")
    @Operation(
            summary = "Получить профиль пользователя",
            description = "Возвращает профильную информацию пользователя"
    )
    public ResponseEntity<UserDto> getUserProfile(
            @Parameter(description = "ID пользователя", example = "1", required = true)
            @PathVariable Long id) {
        // В реальном приложении здесь будет вызов сервиса
        return ResponseEntity.ok().build();
    }
}