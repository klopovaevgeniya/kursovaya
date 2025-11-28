package com.example.AutoDetail.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 🔒 Разрешаем общедоступные страницы
                .authorizeHttpRequests(authz -> authz
                        // Swagger UI и API документация
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // API endpoints менеджера (требуют аутентификации)
                        .requestMatchers(
                                "/manager/api/**"
                        ).hasRole("MANAGER")

                        // Общедоступные эндпоинты API (только для чтения)
                        .requestMatchers(
                                "/api/cars/**",
                                "/api/car-details/**",
                                "/api/categories/**",
                                "/api/suppliers/**",
                                "/api/items/**",
                                "/api/order-statuses/**"
                        ).permitAll()

                        // Остальные общедоступные страницы
                        .requestMatchers("/", "/auth/**", "/css/**", "/js/**", "/images/**", "/error").permitAll()

                        // Защищенные пути по ролям
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/manager/**").hasRole("MANAGER")
                        .requestMatchers("/client/**").hasRole("CLIENT")

                        // Остальные API эндпоинты требуют аутентификации
                        .requestMatchers("/api/clients/**", "/api/orders/**", "/api/order-items/**",
                                "/api/cart/**", "/api/search-history/**", "/api/users/**").authenticated()

                        .anyRequest().authenticated()
                )

                // 🔑 Настройка формы логина
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .successHandler(new CustomAuthenticationSuccessHandler())
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )

                // 🚪 Настройка выхода
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                // ⚠️ Обработка ошибок доступа
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/auth/access-denied")
                )

                // ✅ Отключаем CSRF для API (можно включить при необходимости)
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                "/api/**",  // Отключаем CSRF для всех API эндпоинтов
                                "/manager/api/**", // Отключаем CSRF для API менеджера
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        )
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}