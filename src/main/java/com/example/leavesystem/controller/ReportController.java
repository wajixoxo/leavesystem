package com.example.leavesystem.controller;

import com.example.leavesystem.dto.DepartmentEmployeeCountRequest;
import com.example.leavesystem.dto.LeaveStatusSummaryRequest;
import com.example.leavesystem.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/employees-by-department")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(description = "Get employees by department (only ADMIN, MANAGER)")
    public ResponseEntity<List<DepartmentEmployeeCountRequest>> getEmployeeCountByDepartment() {
        return ResponseEntity.ok(reportService.getEmployeeCountByDepartment());
    }

    @GetMapping("/leave-status-summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(description = "Get summary of leave status (only ADMIN, MANAGER)")
    public ResponseEntity<LeaveStatusSummaryRequest> getLeaveStatusSummary() {
        return ResponseEntity.ok(reportService.getLeaveStatusSummary());
    }
}
