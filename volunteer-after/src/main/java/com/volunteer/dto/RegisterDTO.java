package com.volunteer.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String username;
    private String password;
    private String phone;

    /**
     * 技能标签。用 Object 接收，兼容两种前端写法：
     * 数组 ["医疗急救","摄影"]，或字符串 "医疗急救,摄影"。
     * 落库时由 UserService 统一拼接为逗号分隔字符串。
     */
    private Object skills;
}
