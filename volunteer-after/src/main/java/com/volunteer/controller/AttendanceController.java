package com.volunteer.controller;

import com.volunteer.common.Result;
import com.volunteer.service.AttendanceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/my")
    public Result<?> my(HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        return Result.success(attendanceService.myHours(userId));
    }
}
