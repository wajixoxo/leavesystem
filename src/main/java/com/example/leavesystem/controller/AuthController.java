package com.example.leavesystem.controller;

import com.example.leavesystem.dto.LoginRequest;
import com.example.leavesystem.dto.RegisterRequest;
import com.example.leavesystem.entity.User;
import com.example.leavesystem.repository.UserRepository;
import com.example.leavesystem.security.JwtUtil;
import com.example.leavesystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authenticate")
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(PasswordEncoder passwordEncoder, UserRepository userRepository, UserService userService, JwtUtil jwtUtil) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    @Operation(description = "Registering new users")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest request) {
        User user = userService.registerNewUser(request);
        return ResponseEntity.ok("User registered: " + user.getUsername());
    }

    @PostMapping("/login")
    @Operation(description = "Logins user and returns JWT token")
    public ResponseEntity<String> loginUser(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail());

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok("Token is "+token);
    }
}
