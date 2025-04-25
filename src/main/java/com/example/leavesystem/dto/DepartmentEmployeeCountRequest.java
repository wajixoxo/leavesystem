package com.example.leavesystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentEmployeeCountRequest {
    private Long departmentId;
    private String departmentName;
    private Long employeeCount;
}
