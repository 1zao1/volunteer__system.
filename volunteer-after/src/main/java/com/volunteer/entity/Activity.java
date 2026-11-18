package com.volunteer.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("activity")
public class Activity {
    @TableId(type = IdType.AUTO) private Long id;
    private String title;
    private String category;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxCount;
    private Integer signedCount;
    private Integer hours;
    private String description;
    private String status;
    private Long organizerId;
    private LocalDateTime createTime;
}