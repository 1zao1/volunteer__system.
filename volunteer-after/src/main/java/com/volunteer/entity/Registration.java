package com.volunteer.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("registration")
public class Registration {
    @TableId(type = IdType.AUTO) private Long id;
    private Long userId;
    private Long activityId;
    private String status;
    private LocalDateTime signupTime;
}