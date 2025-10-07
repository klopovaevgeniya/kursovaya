package com.example.AutoDetail.dto;

import com.example.AutoDetail.entity.Role;
import java.time.LocalDateTime;

public class UserDto {
    private Long id;
    private String name;
    private String surname;
    private String patronymic;
    private String login;
    private Role role;
    private String description;

    // Конструкторы, геттеры и сеттеры
    public UserDto() {}

    public UserDto(Long id, String name, String surname, String patronymic,
                   String login, Role role, String description) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.login = login;
        this.role = role;
        this.description = description;
    }

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
}