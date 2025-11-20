package com.example.AutoDetail.controller.admin;

import com.example.AutoDetail.service.BackupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/backup")
public class BackupController {

    private static final Logger logger = LoggerFactory.getLogger(BackupController.class);

    private final BackupService backupService;

    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }

    @GetMapping
    public String backupPage(Model model) {
        logger.info("Запрос страницы управления резервным копированием");
        try {
            model.addAttribute("title", "Управление резервным копированием");
            model.addAttribute("backupInfo", backupService.getBackupInfo());
            logger.debug("Страница резервного копирования успешно загружена");
            return "admin/backup";
        } catch (Exception e) {
            logger.error("Ошибка загрузки страницы резервного копирования: {}", e.getMessage(), e);
            model.addAttribute("error", "Ошибка загрузки страницы: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/manual")
    public String manualBackup(RedirectAttributes redirectAttributes) {
        logger.info("Запрос ручного резервного копирования");
        try {
            backupService.manualBackup();
            logger.info("Ручное резервное копирование успешно запущено");
            redirectAttributes.addFlashAttribute("success", "Ручное резервное копирование запущено");
        } catch (Exception e) {
            logger.error("Ошибка при ручном резервном копировании: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при резервном копировании: " + e.getMessage());
        }
        return "redirect:/admin/backup";
    }
}