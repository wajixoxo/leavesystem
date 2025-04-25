package com.example.leavesystem.repository;

import com.example.leavesystem.entity.LeaveRequest;
import com.example.leavesystem.entity.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface LeaveRepository extends JpaRepository<LeaveRequest, Long>{
   Optional<LeaveRequest> findByLeaveId(Long leaveId);
   List<LeaveRequest> findByEmployeeId(Long Id);

   @Query("SELECT lr.status as status, COUNT(lr) as count FROM LeaveRequest lr GROUP BY lr.status")
   List<Object[]> countLeaveRequestsByStatus();

   default Map<LeaveStatus, Long> countByStatus() {
      return countLeaveRequestsByStatus().stream()
              .collect(Collectors.toMap(
                      row -> (LeaveStatus) row[0],
                      row -> (Long) row[1]
              ));
   }
}
