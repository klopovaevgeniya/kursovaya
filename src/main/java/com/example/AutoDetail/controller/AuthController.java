package com.example.AutoDetail.controller;

import com.example.AutoDetail.entity.Client;
import com.example.AutoDetail.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) Boolean error,
                            @RequestParam(value = "logout", required = false) Boolean logout,
                            @RequestParam(value = "registered", required = false) Boolean registered,
                            Model model) {
        logger.info("Запрос страницы авторизации: error={}, logout={}, registered={}", error, logout, registered);

        if (Boolean.TRUE.equals(error)) {
            logger.warn("Попытка входа с неверными учетными данными");
            model.addAttribute("error", "❌ Неверный логин или пароль");
        }
        if (Boolean.TRUE.equals(logout)) {
            logger.info("Пользователь вышел из системы");
            model.addAttribute("message", "✅ Вы успешно вышли из системы");
        }
        if (Boolean.TRUE.equals(registered)) {
            logger.info("Пользователь успешно зарегистрирован");
            model.addAttribute("success", "✅ Регистрация прошла успешно! Теперь вы можете войти.");
        }

        logger.debug("Страница авторизации загружена");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        logger.info("Запрос страницы регистрации");
        model.addAttribute("client", new Client());
        logger.debug("Форма регистрации инициализирована");
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerClient(@Valid @ModelAttribute Client client,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        logger.info("Попытка регистрации клиента: логин={}, имя={}, фамилия={}",
                client.getLogin(), client.getName(), client.getSurname());

        // Проверка ошибок валидации
        if (bindingResult.hasErrors()) {
            Map<String, String> validationErrors = getValidationErrors(bindingResult);
            logger.warn("Ошибки валидации при регистрации клиента {}: {}",
                    client.getLogin(), validationErrors);

            // Добавляем ошибки в модель для отображения в форме
            model.addAttribute("validationErrors", validationErrors);
            logger.debug("Возврат формы регистрации с ошибками валидации");
            return "auth/register";
        }

        try {
            // Дополнительная бизнес-валидация
            Map<String, String> businessErrors = validateClientBusinessRules(client);
            if (!businessErrors.isEmpty()) {
                logger.warn("Бизнес-ошибки при регистрации клиента {}: {}",
                        client.getLogin(), businessErrors);
                model.addAttribute("businessErrors", businessErrors);
                logger.debug("Возврат формы регистрации с бизнес-ошибками");
                return "auth/register";
            }

            // Регистрация клиента
            logger.debug("Начало процесса регистрации клиента {}", client.getLogin());
            boolean registrationSuccess = authService.registerClient(client);

            if (!registrationSuccess) {
                logger.warn("Регистрация клиента {} не удалась: логин, телефон или email уже заняты",
                        client.getLogin());
                model.addAttribute("error", "❌ Логин, телефон или email уже заняты");
                return "auth/register";
            }

            logger.info("Успешная регистрация клиента: логин={}, ID={}",
                    client.getLogin(), client.getId());
            redirectAttributes.addFlashAttribute("success",
                    "✅ Регистрация прошла успешно! Теперь вы можете войти в систему.");
            return "redirect:/auth/login?registered=true";

        } catch (Exception e) {
            logger.error("Ошибка при регистрации клиента {}: {}", client.getLogin(), e.getMessage(), e);
            model.addAttribute("error", "❌ Произошла ошибка при регистрации: " + e.getMessage());
            return "auth/register";
        }
    }

    // AJAX endpoint для проверки доступности логина
    @GetMapping("/check-login")
    @ResponseBody
    public Map<String, Object> checkLoginAvailability(@RequestParam String login) {
        logger.debug("AJAX проверка доступности логина: '{}'", login);
        Map<String, Object> response = new HashMap<>();

        try {
            boolean isAvailable = authService.isLoginAvailable(login);
            response.put("available", isAvailable);
            response.put("message", isAvailable ? "Логин доступен" : "Логин уже занят");

            logger.debug("Результат проверки логина '{}': доступен={}", login, isAvailable);
        } catch (Exception e) {
            logger.error("Ошибка проверки доступности логина '{}': {}", login, e.getMessage(), e);
            response.put("available", false);
            response.put("message", "Ошибка проверки логина");
        }

        return response;
    }

    // AJAX endpoint для проверки доступности телефона
    @GetMapping("/check-phone")
    @ResponseBody
    public Map<String, Object> checkPhoneAvailability(@RequestParam String phone) {
        logger.debug("AJAX проверка доступности телефона: '{}'", phone);
        Map<String, Object> response = new HashMap<>();

        try {
            boolean isAvailable = authService.isPhoneAvailable(phone);
            response.put("available", isAvailable);
            response.put("message", isAvailable ? "Телефон доступен" : "Телефон уже используется");

            logger.debug("Результат проверки телефона '{}': доступен={}", phone, isAvailable);
        } catch (Exception e) {
            logger.error("Ошибка проверки доступности телефона '{}': {}", phone, e.getMessage(), e);
            response.put("available", false);
            response.put("message", "Ошибка проверки телефона");
        }

        return response;
    }

    // AJAX endpoint для проверки доступности email
    @GetMapping("/check-email")
    @ResponseBody
    public Map<String, Object> checkEmailAvailability(@RequestParam String email) {
        logger.debug("AJAX проверка доступности email: '{}'", email);
        Map<String, Object> response = new HashMap<>();

        try {
            boolean isAvailable = authService.isEmailAvailable(email);
            response.put("available", isAvailable);
            response.put("message", isAvailable ? "Email доступен" : "Email уже используется");

            logger.debug("Результат проверки email '{}': доступен={}", email, isAvailable);
        } catch (Exception e) {
            logger.error("Ошибка проверки доступности email '{}': {}", email, e.getMessage(), e);
            response.put("available", false);
            response.put("message", "Ошибка проверки email");
        }

        return response;
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        logger.warn("Запрос страницы 'Доступ запрещен'");
        return "auth/access-denied";
    }

    // Вспомогательные методы

    /**
     * Получение ошибок валидации в виде Map
     */
    private Map<String, String> getValidationErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        logger.trace("Извлечено ошибок валидации: {}", errors.size());
        return errors;
    }

    /**
     * Дополнительная бизнес-валидация
     */
    private Map<String, String> validateClientBusinessRules(Client client) {
        Map<String, String> errors = new HashMap<>();
        logger.debug("Начало бизнес-валидации клиента {}", client.getLogin());

        // Проверка сложности пароля
        if (client.getPassword() != null && client.getPassword().length() < 6) {
            errors.put("password", "Пароль должен содержать не менее 6 символов");
            logger.debug("Пароль слишком короткий: {} символов", client.getPassword().length());
        }

        // Проверка на простой пароль (можно добавить более сложную логику)
        if (client.getPassword() != null && isWeakPassword(client.getPassword())) {
            errors.put("password", "Пароль слишком простой. Используйте буквы, цифры и специальные символы");
            logger.debug("Пароль признан слабым");
        }

        // Проверка соответствия логина требованиям
        if (client.getLogin() != null && !isValidLogin(client.getLogin())) {
            errors.put("login", "Логин должен содержать только латинские буквы, цифры и подчеркивания");
            logger.debug("Логин не соответствует требованиям: {}", client.getLogin());
        }

        // Проверка формата телефона
        if (client.getPhone() != null && !isValidPhoneFormat(client.getPhone())) {
            errors.put("phone", "Неверный формат телефона");
            logger.debug("Неверный формат телефона: {}", client.getPhone());
        }

        // Проверка формата email (если указан)
        if (client.getEmail() != null && !client.getEmail().trim().isEmpty() &&
                !isValidEmailFormat(client.getEmail())) {
            errors.put("email", "Неверный формат email");
            logger.debug("Неверный формат email: {}", client.getEmail());
        }

        logger.debug("Бизнес-валидация завершена: найдено {} ошибок", errors.size());
        return errors;
    }

    /**
     * Проверка сложности пароля
     */
    private boolean isWeakPassword(String password) {
        // Простая проверка - можно добавить более сложную логику
        boolean isWeak = password.length() < 6 ||
                password.equals(password.toLowerCase()) || // только нижний регистр
                password.matches("[0-9]+") || // только цифры
                password.matches("[a-zA-Z]+"); // только буквы

        logger.trace("Проверка сложности пароля: слабый={}", isWeak);
        return isWeak;
    }

    /**
     * Проверка формата логина
     */
    private boolean isValidLogin(String login) {
        boolean isValid = login.matches("^[a-zA-Z0-9_]{3,50}$");
        logger.trace("Проверка формата логина '{}': валиден={}", login, isValid);
        return isValid;
    }

    /**
     * Проверка формата телефона
     */
    private boolean isValidPhoneFormat(String phone) {
        boolean isValid = phone.matches("^[\\+]?[0-9\\-\\(\\)\\s]{7,15}$");
        logger.trace("Проверка формата телефона '{}': валиден={}", phone, isValid);
        return isValid;
    }

    /**
     * Проверка формата email
     */
    private boolean isValidEmailFormat(String email) {
        boolean isValid = email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        logger.trace("Проверка формата email '{}': валиден={}", email, isValid);
        return isValid;
    }
}