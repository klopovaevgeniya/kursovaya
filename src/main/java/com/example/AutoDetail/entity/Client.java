package com.example.AutoDetail.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ\\s-]+$", message = "Имя должно содержать только буквы, пробелы и дефисы")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Фамилия обязательна")
    @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ\\s-]+$", message = "Фамилия должна содержать только буквы, пробелы и дефисы")
    @Column(nullable = false)
    private String surname;

    @Size(max = 50, message = "Отчество не должно превышать 50 символов")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ\\s-]*$", message = "Отчество должно содержать только буквы, пробелы и дефисы")
    private String patronymic;

    @NotBlank(message = "Телефон обязателен")
    @Pattern(regexp = "^[\\+]?[0-9\\-\\(\\)\\s]{7,15}$", message = "Неверный формат телефона")
    @Column(nullable = false, unique = true)
    private String phone;

    @Email(message = "Некорректный формат email")
    @Column(unique = true)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_car")
    private Car car;

    @NotBlank(message = "Логин обязателен")
    @Size(min = 3, max = 50, message = "Логин должен быть от 3 до 50 символов")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Логин может содержать только латинские буквы, цифры и подчеркивания")
    @Column(nullable = false, unique = true)
    private String login;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, max = 100, message = "Пароль должен быть от 6 до 100 символов")
    @Column(nullable = false)
    private String password;

    // Дополнительные поля для улучшения функциональности
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    // Конструкторы
    public Client() {
        this.isActive = true;
        this.createdAt = java.time.LocalDateTime.now();
        this.updatedAt = java.time.LocalDateTime.now();
    }

    public Client(String name, String surname, String patronymic, String phone, String email,
                  Car car, String login, String password) {
        this();
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.phone = phone;
        this.email = email;
        this.car = car;
        this.login = login;
        this.password = password;
    }

    public Client(String name, String surname, String phone, String login, String password) {
        this();
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.login = login;
        this.password = password;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname != null ? surname.trim() : null;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic != null ? patronymic.trim() : null;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone != null ? phone.trim() : null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : null;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login != null ? login.trim() : null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsActive() {
        return isActive != null ? isActive : true;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive != null ? isActive : true;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public java.time.LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.time.LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Методы жизненного цикла
    @PrePersist
    protected void onCreate() {
        this.createdAt = java.time.LocalDateTime.now();
        this.updatedAt = java.time.LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = java.time.LocalDateTime.now();
    }

    // Бизнес-методы
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

    public String getShortName() {
        StringBuilder shortName = new StringBuilder();
        if (surname != null && !surname.trim().isEmpty()) {
            shortName.append(surname);
        }
        if (name != null && !name.trim().isEmpty()) {
            if (shortName.length() > 0) shortName.append(" ");
            shortName.append(name.charAt(0)).append(".");
        }
        return shortName.length() > 0 ? shortName.toString() : "Клиент";
    }

    public boolean hasCar() {
        return car != null;
    }

    public boolean hasEmail() {
        return email != null && !email.trim().isEmpty();
    }

    public void deactivate() {
        this.isActive = false;
        this.updatedAt = java.time.LocalDateTime.now();
    }

    public void activate() {
        this.isActive = true;
        this.updatedAt = java.time.LocalDateTime.now();
    }

    // Валидационные методы
    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
                surname != null && !surname.trim().isEmpty() &&
                phone != null && !phone.trim().isEmpty() &&
                login != null && !login.trim().isEmpty() &&
                password != null && !password.trim().isEmpty() &&
                isValidPhoneFormat() &&
                isValidLoginFormat() &&
                (email == null || email.trim().isEmpty() || isValidEmailFormat());
    }

    public boolean isValidPhoneFormat() {
        return phone != null && phone.matches("^[\\+]?[0-9\\-\\(\\)\\s]{7,15}$");
    }

    public boolean isValidEmailFormat() {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public boolean isValidLoginFormat() {
        return login != null && login.matches("^[a-zA-Z0-9_]{3,50}$");
    }

    public boolean isValidNameFormat() {
        return name != null && name.matches("^[a-zA-Zа-яА-ЯёЁ\\s-]{2,50}$");
    }

    public boolean isValidSurnameFormat() {
        return surname != null && surname.matches("^[a-zA-Zа-яА-ЯёЁ\\s-]{2,50}$");
    }

    public boolean isValidPatronymicFormat() {
        return patronymic == null || patronymic.trim().isEmpty() ||
                patronymic.matches("^[a-zA-Zа-яА-ЯёЁ\\s-]{0,50}$");
    }

    // Методы для получения информации о валидации
    public java.util.Map<String, String> getValidationErrors() {
        java.util.Map<String, String> errors = new java.util.HashMap<>();

        if (name == null || name.trim().isEmpty()) {
            errors.put("name", "Имя обязательно");
        } else if (!isValidNameFormat()) {
            errors.put("name", "Имя должно содержать только буквы, пробелы и дефисы (2-50 символов)");
        }

        if (surname == null || surname.trim().isEmpty()) {
            errors.put("surname", "Фамилия обязательна");
        } else if (!isValidSurnameFormat()) {
            errors.put("surname", "Фамилия должна содержать только буквы, пробелы и дефисы (2-50 символов)");
        }

        if (!isValidPatronymicFormat()) {
            errors.put("patronymic", "Отчество должно содержать только буквы, пробелы и дефисы (до 50 символов)");
        }

        if (phone == null || phone.trim().isEmpty()) {
            errors.put("phone", "Телефон обязателен");
        } else if (!isValidPhoneFormat()) {
            errors.put("phone", "Неверный формат телефона");
        }

        if (email != null && !email.trim().isEmpty() && !isValidEmailFormat()) {
            errors.put("email", "Неверный формат email");
        }

        if (login == null || login.trim().isEmpty()) {
            errors.put("login", "Логин обязателен");
        } else if (!isValidLoginFormat()) {
            errors.put("login", "Логин должен содержать только латинские буквы, цифры и подчеркивания (3-50 символов)");
        }

        if (password == null || password.trim().isEmpty()) {
            errors.put("password", "Пароль обязателен");
        } else if (password.length() < 6) {
            errors.put("password", "Пароль должен содержать не менее 6 символов");
        } else if (password.length() > 100) {
            errors.put("password", "Пароль слишком длинный");
        }

        return errors;
    }

    // equals и hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        Client client = (Client) o;
        return id != null && id.equals(client.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // toString
    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", login='" + login + '\'' +
                ", phone='" + phone + '\'' +
                ", isActive=" + isActive +
                '}';
    }

    // Builder метод (альтернатива Lombok)
    public static ClientBuilder builder() {
        return new ClientBuilder();
    }

    public static class ClientBuilder {
        private Long id;
        private String name;
        private String surname;
        private String patronymic;
        private String phone;
        private String email;
        private Car car;
        private String login;
        private String password;
        private Boolean isActive;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime updatedAt;

        public ClientBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ClientBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ClientBuilder surname(String surname) {
            this.surname = surname;
            return this;
        }

        public ClientBuilder patronymic(String patronymic) {
            this.patronymic = patronymic;
            return this;
        }

        public ClientBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public ClientBuilder email(String email) {
            this.email = email;
            return this;
        }

        public ClientBuilder car(Car car) {
            this.car = car;
            return this;
        }

        public ClientBuilder login(String login) {
            this.login = login;
            return this;
        }

        public ClientBuilder password(String password) {
            this.password = password;
            return this;
        }

        public ClientBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public ClientBuilder createdAt(java.time.LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ClientBuilder updatedAt(java.time.LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Client build() {
            Client client = new Client();
            client.setId(this.id);
            client.setName(this.name);
            client.setSurname(this.surname);
            client.setPatronymic(this.patronymic);
            client.setPhone(this.phone);
            client.setEmail(this.email);
            client.setCar(this.car);
            client.setLogin(this.login);
            client.setPassword(this.password);
            client.setIsActive(this.isActive);
            client.setCreatedAt(this.createdAt);
            client.setUpdatedAt(this.updatedAt);
            return client;
        }
    }
}