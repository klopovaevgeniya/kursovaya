package com.example.AutoDetail.service;

import com.example.AutoDetail.entity.Client;
import com.example.AutoDetail.entity.User;
import com.example.AutoDetail.entity.Role;
import com.example.AutoDetail.repository.ClientRepository;
import com.example.AutoDetail.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean registerClient(Client client) {
        if (clientRepository.existsByLogin(client.getLogin()) ||
                clientRepository.existsByPhone(client.getPhone())) {
            return false;
        }

        client.setPassword(passwordEncoder.encode(client.getPassword()));
        clientRepository.save(client);
        return true;
    }

    public boolean registerManager(User user) {
        if (userRepository.existsByLogin(user.getLogin())) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ROLE_MANAGER);
        userRepository.save(user);
        return true;
    }
}