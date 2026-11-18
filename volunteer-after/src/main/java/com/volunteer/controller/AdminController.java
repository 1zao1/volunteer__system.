package com.volunteer.controller;

import com.volunteer.common.Result;
import com.volunteer.entity.*;
import com.volunteer.mapper.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.volunteer.service.AttendanceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class AdminController {
    @Autowired private ActivityMapper activityMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private RegistrationMapper regMapper;
    @Autowired private AttendanceService attendanceService;

    @GetMapping("/admin/dashboard")
    public Result<?> dashboard() {
        Map<String, Object> m = new HashMap<>();
        m.put("totalActivities", activityMapper.selectCount(null));
        m.put("totalVolunteers", userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getRole, "volunteer")));
        m.put("totalHours", 0);
        m.put("pendingAudit", regMapper.selectCount(
                new LambdaQueryWrapper<Registration>().eq(Registration::getStatus, "pending")));
        return Result.success(m);
    }

    @GetMapping("/attendance/my")
    public Result<?> myHours(HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        return Result.success(attendanceService.myHours(userId));
    }
}