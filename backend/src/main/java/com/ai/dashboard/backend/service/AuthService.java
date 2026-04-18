package com.ai.dashboard.backend.service;

import com.ai.dashboard.backend.dto.AuthResponse;
import com.ai.dashboard.backend.dto.LoginRequest;
import com.ai.dashboard.backend.dto.SignUpRequest;
import com.ai.dashboard.backend.model.User;
import com.ai.dashboard.backend.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse signup(SignUpRequest request) {
        // Validate input
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Username is required")
                    .build();
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Email is required")
                    .build();
        }

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Password is required")
                    .build();
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Passwords do not match")
                    .build();
        }

        // Check if user already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Username already exists")
                    .build();
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Email already exists")
                    .build();
        }

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.USER)
                .active(true)
                .build();

        userRepository.save(user);

        // Generate token
        String token = jwtUtil.generateToken(user.getUsername(), user.getEmail(), user.getRole().toString());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .success(true)
                .message("User registered successfully")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // Validate input
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Username is required")
                    .build();
        }

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Password is required")
                    .build();
        }

        // Find user by username
        var user = userRepository.findByUsername(request.getUsername());

        if (user.isEmpty()) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Invalid username or password")
                    .build();
        }

        User foundUser = user.get();

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), foundUser.getPassword())) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Invalid username or password")
                    .build();
        }

        // Check if user is active
        if (!foundUser.getActive()) {
            return AuthResponse.builder()
                    .success(false)
                    .message("User account is disabled")
                    .build();
        }

        // Generate token
        String token = jwtUtil.generateToken(foundUser.getUsername(), foundUser.getEmail(), foundUser.getRole().toString());

        return AuthResponse.builder()
                .token(token)
                .username(foundUser.getUsername())
                .email(foundUser.getEmail())
                .role(foundUser.getRole().toString())
                .success(true)
                .message("Login successful")
                .build();
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
