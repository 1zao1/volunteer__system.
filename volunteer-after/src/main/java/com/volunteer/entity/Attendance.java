package com.volunteer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("attendance")
public class Attendance {
    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 志愿者用户 id */
    private Long userId;

    /** 活动 id */
    private Long activityId;

    /** 本次服务时长（小时，支持 0.5 这种小数） */
    private Double hours;

    /** 状态：confirmed 已确认 / pending 待审核 */
    private String status;

    /** 签到时间 */
    private LocalDateTime checkinTime;

    /** 记录创建时间 */
    private LocalDateTime createTime;
}
