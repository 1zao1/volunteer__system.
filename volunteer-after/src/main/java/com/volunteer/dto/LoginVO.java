package com.volunteer.dto;

import lombok.Data;

@Data
public class LoginVO {
    private String token;
    private Long id;
    private String name;
    private String nickname;
    private String avatar;
    private String signature;
    private Integer creditScore;
    private String role;
}
