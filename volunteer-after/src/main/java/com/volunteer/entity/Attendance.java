package com.volunteer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("attendance")
public class Attendance {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long registrationId;
    private Long userId;
    private Long activityId;
    private Integer hours;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime signTime;
}
