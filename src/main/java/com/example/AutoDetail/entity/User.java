package com.example.AutoDetail.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    private String name;

    @NotBlank(message = "Фамилия обязательна")
    @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
    private String surname;

    @Size(max = 50, message = "Отчество не должно превышать 50 символов")
    private String patronymic;

    @NotBlank(message = "Логин обязателен")
    @Size(min = 3, max = 50, message = "Логин должен быть от 3 до 50 символов")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Логин может содержать только буквы, цифры и подчеркивания")
    @Column(unique = true)
    private String login;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Пароль должен быть не менее 6 символов")
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Роль обязательна")
    private Role role;

    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    private String description;

    @Pattern(regexp = "^[\\+]?[0-9\\-\\(\\)\\s]{7,15}$", message = "Неверный формат телефона")
    private String phone;

    // Конструкторы
    public User() {}

    public User(String name, String surname, String patronymic, String login, String password, Role role, String description, String phone) {
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.login = login;
        this.password = password;
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

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private Long id;
        private String name;
        private String surname;
        private String patronymic;
        private String login;
        private String password;
        private Role role;
        private String description;
        private String phone;

        public UserBuilder id(Long id) { this.id = id; return this; }
        public UserBuilder name(String name) { this.name = name; return this; }
        public UserBuilder surname(String surname) { this.surname = surname; return this; }
        public UserBuilder patronymic(String patronymic) { this.patronymic = patronymic; return this; }
        public UserBuilder login(String login) { this.login = login; return this; }
        public UserBuilder password(String password) { this.password = password; return this; }
        public UserBuilder role(Role role) { this.role = role; return this; }
        public UserBuilder description(String description) { this.description = description; return this; }
        public UserBuilder phone(String phone) { this.phone = phone; return this; }

        public User build() {
            User user = new User();
            user.setId(this.id);
            user.setName(this.name);
            user.setSurname(this.surname);
            user.setPatronymic(this.patronymic);
            user.setLogin(this.login);
            user.setPassword(this.password);
            user.setRole(this.role);
            user.setDescription(this.description);
            user.setPhone(this.phone);
            return user;
        }
    }
}