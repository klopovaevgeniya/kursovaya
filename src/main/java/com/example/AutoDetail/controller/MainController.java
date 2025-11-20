package com.example.AutoDetail.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @GetMapping("/")
    public String home(@RequestParam(value = "logout", required = false) Boolean logout,
                       Model model) {
        logger.info("Запрос главной страницы: logout={}", logout);

        if (Boolean.TRUE.equals(logout)) {
            logger.info("Пользователь вышел из системы - отображение сообщения");
            model.addAttribute("message", "✅ Вы успешно вышли из системы");
        }

        logger.debug("Главная страница успешно загружена");
        return "index";
    }
}