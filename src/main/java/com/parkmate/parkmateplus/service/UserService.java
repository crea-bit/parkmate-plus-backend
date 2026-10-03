package com.parkmate.parkmateplus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.parkmate.parkmateplus.entity.User;
import com.parkmate.parkmateplus.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // =====================================================
    // REGISTER NORMAL USER
    // =====================================================
    public User saveUser(User user) {

        // Normal registration must ALWAYS create USER
        user.setRole("USER");

        // Encode password only if it is not already encoded
        if (user.getPassword() != null
                && !user.getPassword().isBlank()
                && !user.getPassword().startsWith("$2a$")
                && !user.getPassword().startsWith("$2b$")
                && !user.getPassword().startsWith("$2y$")) {

            user.setPassword(
                    passwordEncoder.encode(user.getPassword())
            );
        }

        return userRepository.save(user);
    }

    // =====================================================
    // LOGIN USER / ADMIN
    // =====================================================
    public User login(String email, String password) {

        User user = userRepository.findByEmail(email);

        if (user != null
                && user.getPassword() != null
                && passwordEncoder.matches(
                        password,
                        user.getPassword())) {

            return user;
        }

        return null;
    }

    // =====================================================
    // CREATE ADMIN
    // =====================================================
    public User createAdmin(
            String name,
            String email,
            String password,
            String phone) {

        // Check if email already exists
        User existingUser = userRepository.findByEmail(email);

        if (existingUser != null) {
            throw new RuntimeException(
                    "A user with this email already exists"
            );
        }

        User admin = new User();

        admin.setName(name);
        admin.setEmail(email);
        admin.setPhone(phone);
        admin.setRole("ADMIN");

        // Encode admin password
        admin.setPassword(
                passwordEncoder.encode(password)
        );

        return userRepository.save(admin);
    }
}