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
        if (Boolean.TRUE.equals(error)) {
            model.addAttribute("error", "❌ Неверный логин или пароль");
        }
        if (Boolean.TRUE.equals(logout)) {
            model.addAttribute("message", "✅ Вы успешно вышли из системы");
        }
        if (Boolean.TRUE.equals(registered)) {
            model.addAttribute("success", "✅ Регистрация прошла успешно! Теперь вы можете войти.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("client", new Client());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerClient(@Valid @ModelAttribute Client client,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        logger.info("Попытка регистрации клиента: {}", client.getLogin());

        // Проверка ошибок валидации
        if (bindingResult.hasErrors()) {
            logger.warn("Ошибки валидации при регистрации клиента {}: {}",
                    client.getLogin(), getValidationErrors(bindingResult));

            // Добавляем ошибки в модель для отображения в форме
            model.addAttribute("validationErrors", getValidationErrors(bindingResult));
            return "auth/register";
        }

        try {
            // Дополнительная бизнес-валидация
            Map<String, String> businessErrors = validateClientBusinessRules(client);
            if (!businessErrors.isEmpty()) {
                logger.warn("Бизнес-ошибки при регистрации: {}", businessErrors);
                model.addAttribute("businessErrors", businessErrors);
                return "auth/register";
            }

            // Регистрация клиента
            boolean registrationSuccess = authService.registerClient(client);

            if (!registrationSuccess) {
                model.addAttribute("error", "❌ Логин, телефон или email уже заняты");
                return "auth/register";
            }

            logger.info("Успешная регистрация клиента: {}", client.getLogin());
            redirectAttributes.addFlashAttribute("success",
                    "✅ Регистрация прошла успешно! Теперь вы можете войти в систему.");
            return "redirect:/auth/login?registered=true";

        } catch (Exception e) {
            logger.error("Ошибка при регистрации клиента {}", client.getLogin(), e);
            model.addAttribute("error", "❌ Произошла ошибка при регистрации: " + e.getMessage());
            return "auth/register";
        }
    }

    // AJAX endpoint для проверки доступности логина
    @GetMapping("/check-login")
    @ResponseBody
    public Map<String, Object> checkLoginAvailability(@RequestParam String login) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean isAvailable = authService.isLoginAvailable(login);
            response.put("available", isAvailable);
            response.put("message", isAvailable ? "Логин доступен" : "Логин уже занят");
        } catch (Exception e) {
            response.put("available", false);
            response.put("message", "Ошибка проверки логина");
        }

        return response;
    }

    // AJAX endpoint для проверки доступности телефона
    @GetMapping("/check-phone")
    @ResponseBody
    public Map<String, Object> checkPhoneAvailability(@RequestParam String phone) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean isAvailable = authService.isPhoneAvailable(phone);
            response.put("available", isAvailable);
            response.put("message", isAvailable ? "Телефон доступен" : "Телефон уже используется");
        } catch (Exception e) {
            response.put("available", false);
            response.put("message", "Ошибка проверки телефона");
        }

        return response;
    }

    // AJAX endpoint для проверки доступности email
    @GetMapping("/check-email")
    @ResponseBody
    public Map<String, Object> checkEmailAvailability(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean isAvailable = authService.isEmailAvailable(email);
            response.put("available", isAvailable);
            response.put("message", isAvailable ? "Email доступен" : "Email уже используется");
        } catch (Exception e) {
            response.put("available", false);
            response.put("message", "Ошибка проверки email");
        }

        return response;
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
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
        return errors;
    }

    /**
     * Дополнительная бизнес-валидация
     */
    private Map<String, String> validateClientBusinessRules(Client client) {
        Map<String, String> errors = new HashMap<>();

        // Проверка сложности пароля
        if (client.getPassword() != null && client.getPassword().length() < 6) {
            errors.put("password", "Пароль должен содержать не менее 6 символов");
        }

        // Проверка на простой пароль (можно добавить более сложную логику)
        if (client.getPassword() != null && isWeakPassword(client.getPassword())) {
            errors.put("password", "Пароль слишком простой. Используйте буквы, цифры и специальные символы");
        }

        // Проверка соответствия логина требованиям
        if (client.getLogin() != null && !isValidLogin(client.getLogin())) {
            errors.put("login", "Логин должен содержать только латинские буквы, цифры и подчеркивания");
        }

        // Проверка формата телефона
        if (client.getPhone() != null && !isValidPhoneFormat(client.getPhone())) {
            errors.put("phone", "Неверный формат телефона");
        }

        // Проверка формата email (если указан)
        if (client.getEmail() != null && !client.getEmail().trim().isEmpty() &&
                !isValidEmailFormat(client.getEmail())) {
            errors.put("email", "Неверный формат email");
        }

        return errors;
    }

    /**
     * Проверка сложности пароля
     */
    private boolean isWeakPassword(String password) {
        // Простая проверка - можно добавить более сложную логику
        return password.length() < 6 ||
                password.equals(password.toLowerCase()) || // только нижний регистр
                password.matches("[0-9]+") || // только цифры
                password.matches("[a-zA-Z]+"); // только буквы
    }

    /**
     * Проверка формата логина
     */
    private boolean isValidLogin(String login) {
        return login.matches("^[a-zA-Z0-9_]{3,50}$");
    }

    /**
     * Проверка формата телефона
     */
    private boolean isValidPhoneFormat(String phone) {
        return phone.matches("^[\\+]?[0-9\\-\\(\\)\\s]{7,15}$");
    }

    /**
     * Проверка формата email
     */
    private boolean isValidEmailFormat(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}