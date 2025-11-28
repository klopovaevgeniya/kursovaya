package com.example.AutoDetail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO для клиента")
public class ClientDto {

    @Schema(description = "ID клиента", example = "1")
    private Long id;

    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ\\s-]+$", message = "Имя должно содержать только буквы, пробелы и дефисы")
    @Schema(description = "Имя клиента", example = "Иван")
    private String name;

    @NotBlank(message = "Фамилия обязательна")
    @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ\\s-]+$", message = "Фамилия должна содержать только буквы, пробелы и дефисы")
    @Schema(description = "Фамилия клиента", example = "Иванов")
    private String surname;

    @Size(max = 50, message = "Отчество не должно превышать 50 символов")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ\\s-]*$", message = "Отчество должно содержать только буквы, пробелы и дефисы")
    @Schema(description = "Отчество клиента", example = "Петрович")
    private String patronymic;

    @NotBlank(message = "Телефон обязателен")
    @Pattern(regexp = "^[\\+]?[0-9\\-\\(\\)\\s]{7,15}$", message = "Неверный формат телефона")
    @Schema(description = "Телефон клиента", example = "+7-900-123-45-67")
    private String phone;

    @Email(message = "Некорректный формат email")
    @Schema(description = "Email клиента", example = "ivanov@example.com")
    private String email;

    @Schema(description = "Автомобиль клиента")
    private CarDto car;

    @NotBlank(message = "Логин обязателен")
    @Size(min = 3, max = 50, message = "Логин должен быть от 3 до 50 символов")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Логин может содержать только латинские буквы, цифры и подчеркивания")
    @Schema(description = "Логин клиента", example = "ivanov123")
    private String login;

    @Schema(description = "Дата создания", example = "2023-12-01T10:00:00")
    private String createdAt;

    @Schema(description = "Дата обновления", example = "2023-12-01T10:00:00")
    private String updatedAt;

    // Конструкторы
    public ClientDto() {}

    public ClientDto(Long id, String name, String surname, String patronymic, String phone,
                     String email, CarDto car, String login, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.phone = phone;
        this.email = email;
        this.car = car;
        this.login = login;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public CarDto getCar() { return car; }
    public void setCar(CarDto car) { this.car = car; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // Дополнительные методы для удобства
    @Schema(description = "Полное имя клиента", example = "Иванов Иван Петрович")
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
        return fullName.length() > 0 ? fullName.toString() : "Неизвестный клиент";
    }

    @Schema(description = "Краткое имя клиента", example = "Иванов И.П.")
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
        return shortName.length() > 0 ? shortName.toString() : "Клиент";
    }

    @Schema(description = "Есть ли у клиента автомобиль", example = "true")
    public boolean hasCar() {
        return car != null;
    }

    @Schema(description = "Есть ли у клиента email", example = "true")
    public boolean hasEmail() {
        return email != null && !email.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "ClientDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", login='" + login + '\'' +
                ", phone='" + phone + '\'' +
                ", fullName='" + getFullName() + '\'' +
                '}';
    }
}