package com.volunteer.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String username;
    private String password;
    private String phone;

    /** 注册时自选角色：volunteer / organizer / admin，为空默认 volunteer */
    private String role;

    /**
     * 技能标签。用 Object 接收，兼容两种前端写法：
     * 数组 ["医疗急救","摄影"]，或字符串 "医疗急救,摄影"。
     * 落库时由 UserService 统一拼接为逗号分隔字符串。
     */
    private Object skills;
}
