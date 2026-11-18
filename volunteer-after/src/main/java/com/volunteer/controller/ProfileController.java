package com.volunteer.controller;

import com.volunteer.common.Result;
import com.volunteer.dto.ProfileDTO;
import com.volunteer.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Result<?> get(HttpServletRequest req) {
        Long uid = (Long) req.getAttribute("userId");
        return Result.success(userService.profile(uid));
    }

    @PutMapping
    public Result<?> update(@RequestBody ProfileDTO dto, HttpServletRequest req) {
        userService.updateProfile((Long) req.getAttribute("userId"), dto);
        return Result.success();
    }
}
