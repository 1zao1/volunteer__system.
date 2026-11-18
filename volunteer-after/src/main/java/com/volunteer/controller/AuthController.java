package com.volunteer.controller;

import com.volunteer.common.Result;
import com.volunteer.dto.LoginDTO;
import com.volunteer.dto.LoginVO;
import com.volunteer.dto.RegisterDTO;
import com.volunteer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }
}
