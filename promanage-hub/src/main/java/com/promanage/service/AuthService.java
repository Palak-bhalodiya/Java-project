package com.promanage.service;

import com.promanage.dto.RegistrationDto;
import com.promanage.entity.User;
import com.promanage.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String registerUser(RegistrationDto registrationDto) {
        if (userRepository.findByEmail(registrationDto.email()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(registrationDto.name());
        user.setEmail(registrationDto.email());
        user.setPassword(passwordEncoder.encode(registrationDto.password()));
        user.setRole(registrationDto.role());

        userRepository.save(user);

        return "User registered successfully with role: " + user.getRole();
    }
}
