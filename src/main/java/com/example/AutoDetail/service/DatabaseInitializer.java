package com.example.AutoDetail.service;

import com.example.AutoDetail.entity.User;
import com.example.AutoDetail.entity.Role;
import com.example.AutoDetail.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class DatabaseInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        logger.info("=== Начало инициализации базы данных ===");
        createDefaultAdmin();
        createDefaultManager();
        createTestClient();
        logger.info("=== Завершение инициализации базы данных ===");
    }

    private void createDefaultAdmin() {
        String adminLogin = "admin";
        if (userRepository.findByLogin(adminLogin).isEmpty()) {
            User admin = User.builder()
                    .name("Евгения")
                    .surname("Клопова")
                    .patronymic("Дмитриевна")
                    .login(adminLogin)
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ROLE_ADMIN)
                    .description("Главный администратор системы")
                    .build();

            userRepository.save(admin);
            logger.info("✅ Создан администратор по умолчанию");
            logger.info("👤 Логин: {}", adminLogin);
            logger.info("🔑 Пароль: admin123");
            logger.info("🎯 Роль: Администратор");
        } else {
            logger.info("ℹ️ Администратор уже существует: {}", adminLogin);
        }
    }

    private void createDefaultManager() {
        String managerLogin = "manager";
        if (userRepository.findByLogin(managerLogin).isEmpty()) {
            User manager = User.builder()
                    .name("Артемий")
                    .surname("Лихачёв")
                    .patronymic("Алексеевич")
                    .login(managerLogin)
                    .password(passwordEncoder.encode("manager123"))
                    .role(Role.ROLE_MANAGER)
                    .description("Старший менеджер по продажам")
                    .build();

            userRepository.save(manager);
            logger.info("✅ Создан менеджер по умолчанию");
            logger.info("👤 Логин: {}", managerLogin);
            logger.info("🔑 Пароль: manager123");
            logger.info("🎯 Роль: Менеджер");
        } else {
            logger.info("ℹ️ Менеджер уже существует: {}", managerLogin);
        }
    }

    private void createTestClient() {
        logger.info("ℹ️ Клиенты могут зарегистрироваться через форму регистрации");
        logger.info("📝 URL регистрации: http://localhost:8080/auth/register");
    }
}