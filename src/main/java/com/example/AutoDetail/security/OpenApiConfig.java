package com.example.AutoDetail.security;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация Swagger/OpenAPI для документации REST API
 */
// @Configuration
public class OpenApiConfig {

    // @Bean
    public OpenAPI autoDetailOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AutoDetail API")
                        .description("REST API для системы управления онлайн-магазином автозапчастей")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("AutoDetail Team")
                                .email("support@autodetail.com")));
    }
}