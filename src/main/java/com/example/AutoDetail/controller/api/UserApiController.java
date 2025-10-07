package com.example.AutoDetail.controller.api;

import com.example.AutoDetail.dto.UserDto;
import com.example.AutoDetail.entity.User;
import com.example.AutoDetail.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API для управления пользователями
 * Базовый URL: /api/v1/users
 */
// @RestController
// @RequestMapping("/api/v1/users")
public class UserApiController {

    private final AdminService adminService;

    public UserApiController(AdminService adminService) {
        this.adminService = adminService;
    }

    // @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        try {
            List<UserDto> users = adminService.getAllManagers().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        try {
            return adminService.getManagerById(id)
                    .map(this::convertToDto)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        try {
            if (adminService.isLoginExists(userDto.getLogin(), null)) {
                return ResponseEntity.badRequest().build();
            }
            User user = convertToEntity(userDto);
            User savedUser = adminService.saveManager(user);
            return ResponseEntity.ok(convertToDto(savedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        try {
            if (adminService.isLoginExists(userDto.getLogin(), id)) {
                return ResponseEntity.badRequest().build();
            }
            userDto.setId(id);
            User user = convertToEntity(userDto);
            User updatedUser = adminService.saveManager(user);
            return ResponseEntity.ok(convertToDto(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            adminService.deleteManager(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private UserDto convertToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getPatronymic(),
                user.getLogin(),
                user.getRole(),
                user.getDescription()
        );
    }

    private User convertToEntity(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setPatronymic(userDto.getPatronymic());
        user.setLogin(userDto.getLogin());
        user.setRole(userDto.getRole());
        user.setDescription(userDto.getDescription());
        // Пароль должен устанавливаться отдельно
        return user;
    }
}