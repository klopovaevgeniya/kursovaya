package com.example.AutoDetail.dto;

import com.example.AutoDetail.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO для пользователя системы")
public class UserDto {

    @Schema(description = "ID пользователя", example = "1")
    private Long id;

    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    @Schema(description = "Имя пользователя", example = "Иван")
    private String name;

    @NotBlank(message = "Фамилия обязательна")
    @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
    @Schema(description = "Фамилия пользователя", example = "Иванов")
    private String surname;

    @Size(max = 50, message = "Отчество не должно превышать 50 символов")
    @Schema(description = "Отчество пользователя", example = "Петрович")
    private String patronymic;

    @NotBlank(message = "Логин обязателен")
    @Size(min = 3, max = 50, message = "Логин должен быть от 3 до 50 символов")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Логин может содержать только буквы, цифры и подчеркивания")
    @Schema(description = "Логин пользователя", example = "ivanov_admin")
    private String login;

    @NotNull(message = "Роль обязательна")
    @Schema(description = "Роль пользователя", example = "ADMIN")
    private Role role;

    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    @Schema(description = "Описание пользователя", example = "Старший администратор системы")
    private String description;

    @Pattern(regexp = "^[\\+]?[0-9\\-\\(\\)\\s]{7,15}$", message = "Неверный формат телефона")
    @Schema(description = "Телефон пользователя", example = "+7-900-123-45-67")
    private String phone;

    // Конструкторы
    public UserDto() {}

    public UserDto(Long id, String name, String surname, String patronymic, String login,
                   Role role, String description, String phone) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.login = login;
        this.role = role;
        this.description = description;
        this.phone = phone;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getPatronymic() { return patronymic; }
    public void setPatronymic(String patronymic) { this.patronymic = patronymic; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    // Дополнительные методы для удобства
    @Schema(description = "Полное имя пользователя", example = "Иванов Иван Петрович")
    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        if (surname != null && !surname.trim().isEmpty()) {
            fullName.append(surname);
        }
        if (name != null && !name.trim().isEmpty()) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(name);
        }
        if (patronymic != null && !patronymic.trim().isEmpty()) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(patronymic);
        }
        return fullName.length() > 0 ? fullName.toString() : "Неизвестный пользователь";
    }

    @Schema(description = "Краткое имя пользователя", example = "Иванов И.П.")
    public String getShortName() {
        StringBuilder shortName = new StringBuilder();
        if (surname != null && !surname.trim().isEmpty()) {
            shortName.append(surname);
        }
        if (name != null && !name.trim().isEmpty()) {
            if (shortName.length() > 0) shortName.append(" ");
            shortName.append(name.charAt(0)).append(".");
        }
        if (patronymic != null && !patronymic.trim().isEmpty()) {
            if (shortName.length() > 0) shortName.append(" ");
            shortName.append(patronymic.charAt(0)).append(".");
        }
        return shortName.length() > 0 ? shortName.toString() : "Пользователь";
    }

    @Schema(description = "Есть ли у пользователя телефон", example = "true")
    public boolean hasPhone() {
        return phone != null && !phone.trim().isEmpty();
    }

    @Schema(description = "Есть ли у пользователя описание", example = "true")
    public boolean hasDescription() {
        return description != null && !description.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", login='" + login + '\'' +
                ", role=" + role +
                ", fullName='" + getFullName() + '\'' +
                '}';
    }
}