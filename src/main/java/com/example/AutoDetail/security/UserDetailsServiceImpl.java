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
        // Сначала ищем в таблице пользователей (админы и менеджеры)
        User user = userRepository.findByLogin(username)
                .orElse(null);

        if (user != null) {
            return new org.springframework.security.core.userdetails.User(
                    user.getLogin(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
            );
        }

        // Если не нашли, ищем в таблице клиентов
        Client client = clientRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        return new org.springframework.security.core.userdetails.User(
                client.getLogin(),
                client.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT"))
        );
    }
}