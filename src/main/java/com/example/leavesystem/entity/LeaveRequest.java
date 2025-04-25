package com.example.leavesystem.entity;
import jakarta.persistence.*;
import com.example.leavesystem.entity.LeaveStatus;
import com.example.leavesystem.entity.LeaveReason;
import java.time.LocalDate;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "leave_request")

public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaveId;

    private Long employeeId;

    @Enumerated(EnumType.STRING)
    private LeaveReason reason;

    private LocalDate startDate;
    private LocalDate endDate;


    @Enumerated(EnumType.STRING)
    @Builder.Default
    private LeaveStatus status = LeaveStatus.PENDING;

}
