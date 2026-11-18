package com.volunteer.dto;

import lombok.Data;

@Data
public class ProfileDTO {
    private String nickname;
    private String signature;
    /** 预设头像标识或图片 dataURL，长度限制由服务端校验 */
    private String avatar;
}
