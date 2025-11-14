package com.example.AutoDetail.service;

import com.example.AutoDetail.entity.Client;
import com.example.AutoDetail.entity.User;
import com.example.AutoDetail.entity.Role;
import com.example.AutoDetail.repository.ClientRepository;
import com.example.AutoDetail.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    // Регулярные выражения для валидации
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[\\+]?[0-9\\-\\(\\)\\s]{7,15}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern LOGIN_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,50}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Zа-яА-ЯёЁ\\s-]{2,50}$");

    public AuthService(UserRepository userRepository, ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public boolean registerClient(Client client) {
        try {
            logger.info("Начало регистрации клиента: {}", client.getLogin());

            // Валидация данных клиента
            validateClientData(client);

            // Проверка уникальности данных
            if (!isClientDataUnique(client)) {
                logger.warn("Данные клиента не уникальны: login={}, phone={}, email={}",
                        client.getLogin(), client.getPhone(), client.getEmail());
                return false;
            }

            // Шифрование пароля
            client.setPassword(passwordEncoder.encode(client.getPassword()));

            // Сохранение клиента
            Client savedClient = clientRepository.save(client);
            logger.info("Клиент успешно зарегистрирован: ID={}, login={}",
                    savedClient.getId(), savedClient.getLogin());

            return true;

        } catch (Exception e) {
            logger.error("Ошибка при регистрации клиента: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка регистрации: " + e.getMessage(), e);
        }
    }

    @Transactional
    public boolean registerManager(User user) {
        try {
            logger.info("Начало регистрации менеджера: {}", user.getLogin());

            // Валидация данных менеджера
            validateManagerData(user);

            // Проверка уникальности логина
            if (userRepository.existsByLogin(user.getLogin())) {
                logger.warn("Логин менеджера уже занят: {}", user.getLogin());
                return false;
            }

            // Шифрование пароля
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(Role.ROLE_MANAGER);

            // Сохранение менеджера
            User savedUser = userRepository.save(user);
            logger.info("Менеджер успешно зарегистрирован: ID={}, login={}",
                    savedUser.getId(), savedUser.getLogin());

            return true;

        } catch (Exception e) {
            logger.error("Ошибка при регистрации менеджера: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка регистрации менеджера: " + e.getMessage(), e);
        }
    }

    // Методы проверки доступности данных

    public boolean isLoginAvailable(String login) {
        if (login == null || login.trim().isEmpty()) {
            return false;
        }

        // Проверка формата логина
        if (!LOGIN_PATTERN.matcher(login).matches()) {
            return false;
        }

        // Проверка уникальности
        return !clientRepository.existsByLogin(login) && !userRepository.existsByLogin(login);
    }

    public boolean isPhoneAvailable(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }

        // Проверка формата телефона
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            return false;
        }

        // Проверка уникальности
        return !clientRepository.existsByPhone(phone);
    }

    public boolean isEmailAvailable(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true; // Email не обязателен
        }

        // Проверка формата email
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return false;
        }

        // Проверка уникальности
        Optional<Client> existingClient = clientRepository.findByEmail(email);
        return existingClient.isEmpty();
    }

    // Вспомогательные методы валидации

    /**
     * Валидация данных клиента
     */
    private void validateClientData(Client client) {
        if (client == null) {
            throw new RuntimeException("Данные клиента не могут быть пустыми");
        }

        // Валидация имени
        if (client.getName() == null || client.getName().trim().isEmpty()) {
            throw new RuntimeException("Имя обязательно");
        }
        if (!NAME_PATTERN.matcher(client.getName()).matches()) {
            throw new RuntimeException("Имя должно содержать только буквы, пробелы и дефисы (2-50 символов)");
        }

        // Валидация фамилии
        if (client.getSurname() == null || client.getSurname().trim().isEmpty()) {
            throw new RuntimeException("Фамилия обязательна");
        }
        if (!NAME_PATTERN.matcher(client.getSurname()).matches()) {
            throw new RuntimeException("Фамилия должна содержать только буквы, пробелы и дефисы (2-50 символов)");
        }

        // Валидация отчества (необязательное поле)
        if (client.getPatronymic() != null && !client.getPatronymic().trim().isEmpty()) {
            if (!NAME_PATTERN.matcher(client.getPatronymic()).matches()) {
                throw new RuntimeException("Отчество должно содержать только буквы, пробелы и дефисы (2-50 символов)");
            }
        }

        // Валидация телефона
        if (client.getPhone() == null || client.getPhone().trim().isEmpty()) {
            throw new RuntimeException("Телефон обязателен");
        }
        if (!PHONE_PATTERN.matcher(client.getPhone()).matches()) {
            throw new RuntimeException("Неверный формат телефона");
        }

        // Валидация email (необязательное поле)
        if (client.getEmail() != null && !client.getEmail().trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(client.getEmail()).matches()) {
                throw new RuntimeException("Неверный формат email");
            }
        }

        // Валидация логина
        if (client.getLogin() == null || client.getLogin().trim().isEmpty()) {
            throw new RuntimeException("Логин обязателен");
        }
        if (!LOGIN_PATTERN.matcher(client.getLogin()).matches()) {
            throw new RuntimeException("Логин должен содержать только латинские буквы, цифры и подчеркивания (3-50 символов)");
        }

        // Валидация пароля
        if (client.getPassword() == null || client.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Пароль обязателен");
        }
        if (client.getPassword().length() < 6) {
            throw new RuntimeException("Пароль должен содержать не менее 6 символов");
        }
        if (client.getPassword().length() > 100) {
            throw new RuntimeException("Пароль слишком длинный");
        }
    }

    /**
     * Валидация данных менеджера
     */
    private void validateManagerData(User user) {
        if (user == null) {
            throw new RuntimeException("Данные менеджера не могут быть пустыми");
        }

        // Валидация имени
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new RuntimeException("Имя обязательно");
        }
        if (!NAME_PATTERN.matcher(user.getName()).matches()) {
            throw new RuntimeException("Имя должно содержать только буквы, пробелы и дефисы (2-50 символов)");
        }

        // Валидация фамилии
        if (user.getSurname() == null || user.getSurname().trim().isEmpty()) {
            throw new RuntimeException("Фамилия обязательна");
        }
        if (!NAME_PATTERN.matcher(user.getSurname()).matches()) {
            throw new RuntimeException("Фамилия должна содержать только буквы, пробелы и дефисы (2-50 символов)");
        }

        // Валидация логина
        if (user.getLogin() == null || user.getLogin().trim().isEmpty()) {
            throw new RuntimeException("Логин обязателен");
        }
        if (!LOGIN_PATTERN.matcher(user.getLogin()).matches()) {
            throw new RuntimeException("Логин должен содержать только латинские буквы, цифры и подчеркивания (3-50 символов)");
        }

        // Валидация пароля
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Пароль обязателен");
        }
        if (user.getPassword().length() < 6) {
            throw new RuntimeException("Пароль должен содержать не менее 6 символов");
        }
    }

    /**
     * Проверка уникальности данных клиента
     */
    private boolean isClientDataUnique(Client client) {
        // Проверка логина
        if (clientRepository.existsByLogin(client.getLogin()) ||
                userRepository.existsByLogin(client.getLogin())) {
            return false;
        }

        // Проверка телефона
        if (clientRepository.existsByPhone(client.getPhone())) {
            return false;
        }

        // Проверка email (если указан)
        if (client.getEmail() != null && !client.getEmail().trim().isEmpty()) {
            Optional<Client> existingClient = clientRepository.findByEmail(client.getEmail());
            if (existingClient.isPresent()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Полная проверка данных клиента перед регистрацией
     */
    public Map<String, String> validateClientRegistration(Client client) {
        Map<String, String> errors = new HashMap<>();

        try {
            validateClientData(client);
        } catch (RuntimeException e) {
            // Определяем поле для ошибки (упрощенная логика)
            if (e.getMessage().contains("Имя")) {
                errors.put("name", e.getMessage());
            } else if (e.getMessage().contains("Фамилия")) {
                errors.put("surname", e.getMessage());
            } else if (e.getMessage().contains("Телефон")) {
                errors.put("phone", e.getMessage());
            } else if (e.getMessage().contains("Email")) {
                errors.put("email", e.getMessage());
            } else if (e.getMessage().contains("Логин")) {
                errors.put("login", e.getMessage());
            } else if (e.getMessage().contains("Пароль")) {
                errors.put("password", e.getMessage());
            } else {
                errors.put("general", e.getMessage());
            }
        }

        // Проверка уникальности
        if (!errors.containsKey("login") && !isLoginAvailable(client.getLogin())) {
            errors.put("login", "Логин уже занят");
        }

        if (!errors.containsKey("phone") && !isPhoneAvailable(client.getPhone())) {
            errors.put("phone", "Телефон уже используется");
        }

        if (client.getEmail() != null && !client.getEmail().trim().isEmpty() &&
                !errors.containsKey("email") && !isEmailAvailable(client.getEmail())) {
            errors.put("email", "Email уже используется");
        }

        return errors;
    }
}