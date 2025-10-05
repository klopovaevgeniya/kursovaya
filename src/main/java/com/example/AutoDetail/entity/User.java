package com.example.AutoDetail.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Имя обязательно")
    private String name;

    @NotBlank(message = "Фамилия обязательна")
    private String surname;

    private String patronymic;

    @NotBlank(message = "Логин обязателен")
    @Column(unique = true)
    private String login;

    @NotBlank(message = "Пароль обязателен")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String description;

    // Конструкторы
    public User() {}

    public User(String name, String surname, String patronymic, String login, String password, Role role, String description) {
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.login = login;
        this.password = password;
        this.role = role;
        this.description = description;
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

    // Статический метод для builder (альтернатива Lombok)
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    // Builder класс
    public static class UserBuilder {
        private Long id;
        private String name;
        private String surname;
        private String patronymic;
        private String login;
        private String password;
        private Role role;
        private String description;

        public UserBuilder id(Long id) { this.id = id; return this; }
        public UserBuilder name(String name) { this.name = name; return this; }
        public UserBuilder surname(String surname) { this.surname = surname; return this; }
        public UserBuilder patronymic(String patronymic) { this.patronymic = patronymic; return this; }
        public UserBuilder login(String login) { this.login = login; return this; }
        public UserBuilder password(String password) { this.password = password; return this; }
        public UserBuilder role(Role role) { this.role = role; return this; }
        public UserBuilder description(String description) { this.description = description; return this; }

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
            return user;
        }
    }
}