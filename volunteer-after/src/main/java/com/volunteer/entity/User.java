package com.volunteer.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("user")
public class User {
    @TableId(type = IdType.AUTO) private Long id;
    private String username;
    private String password;
    private String phone;
    private String role;
    private String skills;
    private Integer creditScore;
    private LocalDateTime createTime;
}