package com.volunteer.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.volunteer.common.Result;
import com.volunteer.entity.Activity;
import com.volunteer.entity.Registration;
import com.volunteer.entity.User;
import com.volunteer.mapper.ActivityMapper;
import com.volunteer.mapper.RegistrationMapper;
import com.volunteer.mapper.UserMapper;
import com.volunteer.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RegistrationMapper regMapper;
    @Autowired
    private AttendanceService attendanceService;

    /** 首页展示的全站概览，登录后可见 */
    @GetMapping("/overview")
    public Result<?> overview() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("activityCount", activityMapper.selectCount(new LambdaQueryWrapper<Activity>()
                .eq(Activity::getStatus, "published")
                .gt(Activity::getEndTime, LocalDateTime.now())));
        m.put("volunteerCount", userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getRole, "volunteer")));
        m.put("hoursTotal", attendanceService.totalHours());
        m.put("signupCount", regMapper.selectCount(null));
        return Result.success(m);
    }
}
