package com.example.leavesystem.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class DepartmentRequest {

    private Long id;

    @NotBlank(message = "Department name is required")
    private String name;

    private String description;
}