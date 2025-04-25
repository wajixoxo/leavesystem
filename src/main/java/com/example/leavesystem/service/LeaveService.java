//package com.example.leavesystem.service;
//
//import com.example.leavesystem.dto.LeaveRequest;
//import com.example.leavesystem.entity.Leave;
//import com.example.leavesystem.entity.LeaveStatus;
//import com.example.leavesystem.entity.User;
//import com.example.leavesystem.repository.LeaveRepository;
//import com.example.leavesystem.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class LeaveService {
//
//    private final LeaveRepository leaveRepository;
//    private final UserRepository userRepository;
//
//    public Leave createLeave(LeaveRequest leaveRequest, String username) {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        Leave leave = new Leave();
//        leave.setUser(user);
//        leave.setStartDate(leaveRequest.getStartDate());
//        leave.setEndDate(leaveRequest.getEndDate());
//        leave.setReason(leaveRequest.getReason());
//        leave.setStatus(LeaveStatus.PENDING); // Set default status to PENDING
//
//        return leaveRepository.save(leave);
//    }
//
//    public Leave getLeaveById(Long id) {
//        return leaveRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Leave not found"));
//    }
//
//    public List<Leave> getLeavesByEmployeeId(Long employeeId) {
//        return leaveRepository.findByUserId(employeeId);
//    }
//
//    public Leave updateLeaveStatus(Long leaveId, LeaveStatus status) {
//        Leave leave = leaveRepository.findById(leaveId)
//                .orElseThrow(() -> new RuntimeException("Leave not found"));
//
//        // Check if leave is already approved or rejected
//        if (leave.getStatus() == LeaveStatus.APPROVED || leave.getStatus() == LeaveStatus.REJECTED) {
//            throw new IllegalStateException("Cannot modify leave that is already " + leave.getStatus());
//        }
//
//        leave.setStatus(status);
//        return leaveRepository.save(leave);
//    }
//}