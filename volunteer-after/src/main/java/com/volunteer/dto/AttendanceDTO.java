package com.volunteer.dto;

import lombok.Data;

@Data
public class AttendanceDTO {
    private Long registrationId;
    /** 不传则使用活动设置的工时 */
    private Integer hours;
}
