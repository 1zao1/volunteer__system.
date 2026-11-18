package com.volunteer.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.volunteer.common.JwtUtil;
import com.volunteer.dto.*;
import com.volunteer.entity.User;
import com.volunteer.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired private UserMapper userMapper;
    @Autowired private JwtUtil jwtUtil;

    public void register(RegisterDTO dto) {
        if (userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername())) > 0)
            throw new RuntimeException("用户名已存在");
        User u = new User();
        u.setUsername(dto.getUsername());
        u.setPassword(dto.getPassword());
        u.setPhone(dto.getPhone());
        u.setSkills(dto.getSkills());
        u.setRole("volunteer");
        u.setCreditScore(100);
        userMapper.insert(u);
    }

    public LoginVO login(LoginDTO dto) {
        User u = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (u == null || !u.getPassword().equals(dto.getPassword()))
            throw new RuntimeException("用户名或密码错误");
        LoginVO vo = new LoginVO();
        vo.setToken(jwtUtil.generate(u.getId(), u.getRole()));
        vo.setId(u.getId()); vo.setName(u.getUsername()); vo.setRole(u.getRole());
        return vo;
    }
}