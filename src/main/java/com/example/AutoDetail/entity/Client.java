package com.example.AutoDetail.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Имя обязательно")
    private String name;

    @NotBlank(message = "Фамилия обязательна")
    private String surname;

    private String patronymic;

    @NotBlank(message = "Телефон обязателен")
    private String phone;

    private Long idCar;

    @NotBlank(message = "Логин обязателен")
    @Column(unique = true)
    private String login;

    @NotBlank(message = "Пароль обязателен")
    private String password;

    // Конструкторы
    public Client() {}

    public Client(String name, String surname, String patronymic, String phone, Long idCar, String login, String password) {
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.phone = phone;
        this.idCar = idCar;
        this.login = login;
        this.password = password;
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

    public Long getIdCar() { return idCar; }
    public void setIdCar(Long idCar) { this.idCar = idCar; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}