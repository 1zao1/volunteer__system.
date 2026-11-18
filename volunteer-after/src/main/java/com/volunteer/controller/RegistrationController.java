package com.volunteer.controller;

import com.volunteer.common.Result;
import com.volunteer.service.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    private Long uid(HttpServletRequest req) {
        return (Long) req.getAttribute("userId");
    }

    @PostMapping
    public Result<?> signup(@RequestBody Map<String, Long> body, HttpServletRequest req) {
        Long activityId = body.get("activityId");
        if (activityId == null) throw new RuntimeException("缺少活动 id");
        Long id = registrationService.signup(uid(req), activityId, String.valueOf(req.getAttribute("role")));
        return Result.success(id);
    }

    @GetMapping("/my")
    public Result<?> my(HttpServletRequest req) {
        return Result.success(registrationService.myList(uid(req)));
    }

    @DeleteMapping("/{id}")
    public Result<?> cancel(@PathVariable Long id, HttpServletRequest req) {
        registrationService.cancel(uid(req), id);
        return Result.success();
    }
}
