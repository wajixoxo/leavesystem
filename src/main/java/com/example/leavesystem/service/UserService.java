package com.example.leavesystem.service;

import com.example.leavesystem.dto.RegisterRequest;
import com.example.leavesystem.dto.UserRequest;
import com.example.leavesystem.entity.Role;
import com.example.leavesystem.entity.User;
import com.example.leavesystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerNewUser(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        return userRepository.save(user);
    }

    // Get user by username (for /me)
    public UserRequest getUserByUsername(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new RuntimeException("User not found");
        return mapToDto(user);
    }

    public List<UserRequest> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    public List<UserRequest> getUsersByDepartment(String departmentName) {
        return userRepository.findByDepartmentName(departmentName).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    // Get users by role (for admin report)
    public List<UserRequest> getUsersByRole(String roleName) {
        Role role = Role.valueOf(roleName.toUpperCase());
        return userRepository.findByRole(role).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    //Delete user by ID (for admin)
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        userRepository.deleteById(userId);
    }


    public UserRequest mapToDto(User user) {
        UserRequest dto = new UserRequest();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setDepartment(user.getDepartment() != null ? user.getDepartment().getName() : null);
        return dto;
    }
}