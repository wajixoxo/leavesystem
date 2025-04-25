package com.example.leavesystem.dto;
import com.example.leavesystem.entity.LeaveStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@NotNull
@NotBlank
@Getter
@Setter
public class UpdateLeaveStatusRequest {
    private LeaveStatus status;
}
