package com.app.salty.user.dto.response;

import com.app.salty.user.entity.Attendance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceResponse {
    private List<AttendanceDTO> attendanceList;
    private int totalDays;
    public int consecutiveDays; //이번 달 출석일
    public int monthlyRate;
    public Long point;
}
