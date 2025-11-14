package com.example.AutoDetail.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Collection;

/**
 * Обработчик успешной авторизации.
 * Определяет роль пользователя и перенаправляет на нужную страницу.
 */
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String redirectUrl = "/"; // запасной адрес по умолчанию

        if (authentication != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (authorities != null) {
                for (GrantedAuthority authority : authorities) {
                    String role = authority.getAuthority();

                    if ("ROLE_ADMIN".equals(role)) {
                        redirectUrl = "/admin/dashboard";
                        break;
                    } else if ("ROLE_MANAGER".equals(role)) {
                        redirectUrl = "/manager/dashboard";
                        break;
                    } else if ("ROLE_CLIENT".equals(role)) {
                        redirectUrl = "/client/catalog";
                        break;
                    }
                }
            }
        }

        // Логирование
        if (authentication != null && authentication.getName() != null) {
            System.out.println("✅ Успешная авторизация: пользователь=" + authentication.getName() + ", redirect=" + redirectUrl);
        } else {
            System.out.println("✅ Успешная авторизация, redirect=" + redirectUrl);
        }

        // Перенаправление
        response.sendRedirect(redirectUrl);
    }
}
