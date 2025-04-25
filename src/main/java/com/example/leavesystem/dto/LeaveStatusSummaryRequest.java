package com.example.leavesystem.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class LeaveStatusSummaryRequest {
    private Long pending;
    private Long approved;
    private Long rejected;
    private Long total;
}
