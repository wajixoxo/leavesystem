package com.example.leavesystem.controller;

import com.example.leavesystem.dto.UserRequest;
import com.example.leavesystem.entity.Department;
import com.example.leavesystem.entity.Role;
import com.example.leavesystem.entity.User;
import com.example.leavesystem.repository.DepartmentRepository;
import com.example.leavesystem.repository.UserRepository;
import com.example.leavesystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public UserController(UserService userService, UserRepository userRepository, DepartmentRepository departmentRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'ADMIN')")
    @Operation(description = "View own profile")
    public ResponseEntity<UserRequest> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        // Important: userDetails.getUsername() actually returns the email
        return ResponseEntity.ok(userService.getUserByUsername(userDetails.getUsername()));
    }

    @GetMapping
    @Operation(description = "Can view all users (Only ADMIN, MANAGER)")
    public ResponseEntity<List<UserRequest>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/department/{departmentName}")
    @Operation(description = "Get users by department name (Only ADMIN, MANAGER)")
    public ResponseEntity<List<UserRequest>> getUsersByDepartment(@PathVariable String departmentName) {
        return ResponseEntity.ok(userService.getUsersByDepartment(departmentName));
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(description = "Get users by role (Only ADMIN)")
    public ResponseEntity<List<UserRequest>> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @PutMapping("/assign-department/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(description = "Assign department to EMPLOYEE or MANAGER (Only Admin)")
    public ResponseEntity<String> assignDepartmentToStaff(
            @PathVariable Long userId,
            @RequestParam String departmentName) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.EMPLOYEE && user.getRole() != Role.MANAGER) {
            return ResponseEntity.badRequest().body("Only EMPLOYEE or MANAGER can be assigned a department");
        }

        Department department = departmentRepository.findByName(departmentName)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        user.setDepartment(department);
        userRepository.save(user);

        return ResponseEntity.ok("Department '" + departmentName + "' assigned to " + user.getUsername());
    }


    @PostMapping("/create-department")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(description = "Create a new department (Only Admin)")
    public ResponseEntity<String> createDepartment(@RequestParam String name) {
        if (departmentRepository.existsByName(name)) {
            return ResponseEntity.badRequest().body("Department already exists");
        }

        Department dept = new Department();
        dept.setName(name);
        departmentRepository.save(dept);
        return ResponseEntity.ok("Department created: " + name);
    }


    @DeleteMapping("delete-user/{id}")
    @Operation(description = "Delete user (Only ADMIN)")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


}

