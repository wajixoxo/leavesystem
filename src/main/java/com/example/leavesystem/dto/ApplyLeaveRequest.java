package com.example.leavesystem.dto;
import com.example.leavesystem.entity.LeaveReason;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@NotNull
@Getter
@Setter
public class ApplyLeaveRequest {


    public LocalDate startDate;
    public LocalDate endDate;
    public LeaveReason leaveReason;

}
