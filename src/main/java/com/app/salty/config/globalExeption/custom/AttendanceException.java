package com.app.salty.config.globalExeption.custom;

import lombok.Getter;

@Getter
public class AttendanceException extends RuntimeException {
    private final String code;

    public AttendanceException(String message) {
        super(message);
        this.code = "ATTENDANCE_ERROR";
    }
}
