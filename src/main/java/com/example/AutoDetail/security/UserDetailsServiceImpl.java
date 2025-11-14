package com.example.AutoDetail.security;

import com.example.AutoDetail.entity.Client;
import com.example.AutoDetail.entity.User;
import com.example.AutoDetail.repository.ClientRepository;
import com.example.AutoDetail.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    public UserDetailsServiceImpl(UserRepository userRepository, ClientRepository clientRepository) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("🔍 Попытка авторизации пользователя: " + username);
        
        // Сначала ищем в таблице пользователей (админы и менеджеры)
        User user = userRepository.findByLogin(username).orElse(null);

        if (user != null) {
            String role = user.getRole().name(); // уже с префиксом ROLE_
            
            System.out.println("✅ Найден пользователь: " + username + ", роль: " + role);
            
            return new org.springframework.security.core.userdetails.User(
                    user.getLogin(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(role))
            );
        }

        // Если не нашли, ищем в таблице клиентов
        Client client = clientRepository.findByLogin(username).orElse(null);
        
        if (client != null) {
            System.out.println("✅ Найден клиент: " + username);
            
            return new org.springframework.security.core.userdetails.User(
                    client.getLogin(),
                    client.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT"))
            );
        }
        
        System.err.println("❌ Пользователь не найден: " + username);
        throw new UsernameNotFoundException("Пользователь не найден: " + username);
    }
}
