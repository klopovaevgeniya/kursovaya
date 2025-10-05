package com.example.AutoDetail.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(@RequestParam(value = "logout", required = false) Boolean logout,
                       Model model) {
        if (Boolean.TRUE.equals(logout)) {
            model.addAttribute("message", "✅ Вы успешно вышли из системы");
        }
        return "index";
    }
}