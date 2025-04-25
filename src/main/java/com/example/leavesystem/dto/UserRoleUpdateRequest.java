package com.example.leavesystem.dto;
//for admin to update roles
import com.example.leavesystem.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class UserRoleUpdateRequest {
    @NotNull(message = "Role is required")
    private Role role;
}