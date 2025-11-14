package com.example.AutoDetail.service;

import com.example.AutoDetail.entity.Role;
import com.example.AutoDetail.entity.User;
import com.example.AutoDetail.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public List<User> getAllManagers() {
        return userRepository.findByRole(Role.ROLE_MANAGER);
    }
}