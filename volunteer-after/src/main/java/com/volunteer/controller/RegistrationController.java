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
    @Autowired private RegistrationService registrationService;

    @PostMapping
    public Result<?> signup(@RequestBody Map<String, Long> body, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        registrationService.signup(userId, body.get("activityId"));
        return Result.success();
    }

    @GetMapping("/my")
    public Result<?> my(HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        return Result.success(registrationService.myList(userId));
    }
}