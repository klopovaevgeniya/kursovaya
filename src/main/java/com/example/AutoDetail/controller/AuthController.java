package com.example.AutoDetail.controller;

import com.example.AutoDetail.entity.Client;
import com.example.AutoDetail.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
                            Model model) {
        if (Boolean.TRUE.equals(error)) {
            model.addAttribute("error", "❌ Неверный логин или пароль");
        }
        if (Boolean.TRUE.equals(logout)) {
            model.addAttribute("message", "✅ Вы успешно вышли из системы");
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
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            logger.warn("Ошибки валидации при регистрации: {}", result.getAllErrors());
            return "auth/register";
        }

        try {
            if (!authService.registerClient(client)) {
                model.addAttribute("error", "❌ Логин или телефон уже заняты");
                return "auth/register";
            }

            logger.info("Успешная регистрация клиента: {}", client.getLogin());
            model.addAttribute("success", "✅ Регистрация прошла успешно! Теперь вы можете войти.");
            return "auth/login";

        } catch (Exception e) {
            logger.error("Ошибка при регистрации клиента", e);
            model.addAttribute("error", "❌ Произошла ошибка при регистрации. Попробуйте позже.");
            return "auth/register";
        }
    }

    @GetMapping("/login-success")
    public String loginSuccess(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                String role = authority.getAuthority();
                logger.info("Успешный вход пользователя: {} с ролью: {}", username, role);

                if (role.equals("ROLE_ADMIN")) {
                    return "redirect:/admin/dashboard";
                } else if (role.equals("ROLE_MANAGER")) {
                    return "redirect:/manager/dashboard";
                } else if (role.equals("ROLE_CLIENT")) {
                    return "redirect:/client/catalog";
                }
            }
        }
        return "redirect:/auth/login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }
}