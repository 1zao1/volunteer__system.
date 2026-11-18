package com.volunteer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("registration")
public class Registration {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long activityId;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime signupTime;
}
