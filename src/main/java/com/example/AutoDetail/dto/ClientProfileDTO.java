package com.example.AutoDetail.dto;

import jakarta.validation.constraints.*;

public class ClientProfileDTO {

    private Long id;

    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ\\s-]+$", message = "Имя должно содержать только буквы, пробелы и дефисы")
    private String name;

    @NotBlank(message = "Фамилия обязательна")
    @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ\\s-]+$", message = "Фамилия должна содержать только буквы, пробелы и дефисы")
    private String surname;

    @Size(max = 50, message = "Отчество не должно превышать 50 символов")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ\\s-]*$", message = "Отчество должно содержать только буквы, пробелы и дефисы")
    private String patronymic;

    @NotBlank(message = "Телефон обязателен")
    @Pattern(regexp = "^[\\+]?[0-9\\-\\(\\)\\s]{7,15}$", message = "Неверный формат телефона")
    private String phone;

    @Email(message = "Неверный формат email")
    private String email;

    @NotBlank(message = "Логин обязателен")
    @Size(min = 3, max = 50, message = "Логин должен быть от 3 до 50 символов")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Логин может содержать только латинские буквы, цифры и подчеркивания")
    private String login;

    // Поля автомобиля
    @NotBlank(message = "Марка автомобиля обязательна")
    @Size(min = 2, max = 50, message = "Марка автомобиля должна быть от 2 до 50 символов")
    private String carBrand;

    @NotBlank(message = "Модель автомобиля обязательна")
    @Size(min = 1, max = 50, message = "Модель автомобиля должна быть от 1 до 50 символов")
    private String carModel;

    @NotBlank(message = "Тип топлива обязателен")
    @Size(max = 20, message = "Тип топлива не должен превышать 20 символов")
    private String fuelType;

    @NotBlank(message = "Код двигателя обязателен")
    @Size(min = 3, max = 20, message = "Код двигателя должен быть от 3 до 20 символов")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Код двигателя должен содержать только заглавные буквы и цифры")
    private String engineCode;

    @NotBlank(message = "VIN код обязателен")
    @Size(min = 17, max = 17, message = "VIN код должен содержать 17 символов")
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "Неверный формат VIN кода")
    private String vinCode;

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

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getCarBrand() { return carBrand; }
    public void setCarBrand(String carBrand) { this.carBrand = carBrand; }

    public String getCarModel() { return carModel; }
    public void setCarModel(String carModel) { this.carModel = carModel; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public String getEngineCode() { return engineCode; }
    public void setEngineCode(String engineCode) { this.engineCode = engineCode; }

    public String getVinCode() { return vinCode; }
    public void setVinCode(String vinCode) { this.vinCode = vinCode; }
}