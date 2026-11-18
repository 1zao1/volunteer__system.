package com.volunteer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String phone;
    private String role;
    private String skills;
    private Integer creditScore;

    /** 昵称，未设置时前端回落为 username */
    private String nickname;
    /** 个性签名 */
    private String signature;
    /** 头像：预设标识（avatar:bi-person|#0a5c3f）或图片 dataURL */
    private String avatar;
    /** 信誉分恢复计时锚点：每满 2 小时恢复 1 分，直到 85 分封顶 */
    private LocalDateTime creditTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createTime;
}
