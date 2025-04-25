package com.example.leavesystem.controller;

import com.example.leavesystem.dto.ApplyLeaveRequest;
import com.example.leavesystem.dto.UpdateLeaveStatusRequest;
import com.example.leavesystem.entity.LeaveRequest;
import com.example.leavesystem.entity.LeaveStatus;
import com.example.leavesystem.entity.User;
import com.example.leavesystem.repository.LeaveRepository;
import com.example.leavesystem.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/leave")
public class LeaveController {

    @Autowired
    private LeaveRepository leaveRepository;
    @Autowired
    private UserRepository userRepository;



    @PostMapping("/apply")
    @Operation(description = "Employee applying Leaves (only EMPLOYEE, MANAGER)")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER')")
    public ResponseEntity<String> applyLeave(@Valid @RequestBody ApplyLeaveRequest request,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        // Get user by email, not username
        User user = userRepository.findByEmail(userDetails.getUsername());

        LeaveRequest leave = LeaveRequest.builder()
                .employeeId(user.getId())
                .reason(request.getLeaveReason())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(LeaveStatus.PENDING)
                .build(); // status is automatically set to PENDING


        leaveRepository.save(leave);

        System.out.println("Leave applied by employee: " + user.getId());
        System.out.println("Type: " + request.getLeaveReason());
        return ResponseEntity.ok("Leave application submitted");
    }

    @GetMapping("/status/employee/{employeeId}")
    @Operation(description = "Employees to view their own leave requests (only EMPLOYEE, MANAGER)")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER')")
    public List<LeaveRequest> getLeaveStatusEmployee(@PathVariable Long employeeId,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername());
        if (!user.getId().equals(employeeId)) {
            throw new AccessDeniedException("You are not allowed to view other users' leave status.");
        }

        //return a list of leave request made by that specific employee id
        return leaveRepository.findByEmployeeId(user.getId());
    }

    @GetMapping("/status/{leaveId}")
    @Operation(description = "View Specific Leave Application Status (only ADMIN, MANAGER)")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> getLeaveStatus(@PathVariable Long leaveId) {

        //“If the leave request exists, return it with a 200 OK.
        //If not, return a 404 Not Found.”
        return leaveRepository.findByLeaveId(leaveId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/approval/{leaveId}")
    @Operation(description = "Approve/Reject Application Status (only ADMIN, MANAGER)")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> updateLeaveStatus(@PathVariable Long leaveId,
                                               @Valid @RequestBody UpdateLeaveStatusRequest request) {
        return leaveRepository.findByLeaveId(leaveId)
                .map(leave -> {
                    if (leave.getStatus() != LeaveStatus.PENDING) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Leave has already been " + leave.getStatus().name());
                    }

                    leave.setStatus(request.getStatus());
                    leaveRepository.save(leave);
                    return ResponseEntity.ok("Leave " + request.getStatus() + " successfully");
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Leave ID not found"));
    }
}
