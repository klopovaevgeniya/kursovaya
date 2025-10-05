package com.example.AutoDetail.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/client")
public class ClientController {

    @GetMapping("/catalog")
    public String catalog(Model model) {
        model.addAttribute("title", "Каталог автозапчастей");
        return "client/catalog";
    }
}