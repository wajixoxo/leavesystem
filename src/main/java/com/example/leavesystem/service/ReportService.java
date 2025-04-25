package com.example.leavesystem.service;

import com.example.leavesystem.dto.DepartmentEmployeeCountRequest;
import com.example.leavesystem.dto.LeaveStatusSummaryRequest;
import com.example.leavesystem.entity.LeaveStatus;
import com.example.leavesystem.repository.LeaveRepository;
import com.example.leavesystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final UserRepository userRepository;
    private final LeaveRepository leaveRepository;

    @Cacheable(value = "employeesByDepartment")
    public List<DepartmentEmployeeCountRequest> getEmployeeCountByDepartment() {
        return userRepository.countEmployeesByDepartment();
    }

    @Cacheable(value = "leaveStatusSummary")
    public LeaveStatusSummaryRequest getLeaveStatusSummary() {
        Map<LeaveStatus, Long> statusCountMap = leaveRepository.countByStatus();

        LeaveStatusSummaryRequest summaryDto = new LeaveStatusSummaryRequest();
        summaryDto.setPending(statusCountMap.getOrDefault(LeaveStatus.PENDING, 0L));
        summaryDto.setApproved(statusCountMap.getOrDefault(LeaveStatus.APPROVED, 0L));
        summaryDto.setRejected(statusCountMap.getOrDefault(LeaveStatus.REJECTED, 0L));
        summaryDto.setTotal(summaryDto.getPending() + summaryDto.getApproved() + summaryDto.getRejected());

        return summaryDto;
    }
}
