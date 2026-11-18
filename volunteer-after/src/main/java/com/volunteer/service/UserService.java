package com.volunteer.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.volunteer.common.JwtUtil;
import com.volunteer.dto.LoginDTO;
import com.volunteer.dto.LoginVO;
import com.volunteer.dto.RegisterDTO;
import com.volunteer.entity.User;
import com.volunteer.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtUtil jwtUtil;

    public void register(RegisterDTO dto) {
        if (!StringUtils.hasText(dto.getUsername()) || !StringUtils.hasText(dto.getPassword())) {
            throw new RuntimeException("用户名和密码不能为空");
        }
        if (userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername())) > 0) {
            throw new RuntimeException("用户名已存在，换一个试试");
        }
        User u = new User();
        u.setUsername(dto.getUsername());
        u.setPassword(dto.getPassword());
        u.setPhone(dto.getPhone());
        u.setRole("volunteer");
        u.setCreditScore(100);
        u.setSkills(joinSkills(dto.getSkills()));
        userMapper.insert(u);
    }

    /** 兼容数组与逗号分隔字符串两种入参 */
    private String joinSkills(Object skills) {
        if (skills == null) return null;
        if (skills instanceof java.util.Collection<?> c) {
            return c.stream()
                    .map(s -> s == null ? "" : String.valueOf(s).trim())
                    .filter(StringUtils::hasText)
                    .collect(java.util.stream.Collectors.joining(","));
        }
        String s = String.valueOf(skills).trim();
        return StringUtils.hasText(s) ? s : null;
    }

    public LoginVO login(LoginDTO dto) {
        User u = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (u == null || !u.getPassword().equals(dto.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        LoginVO vo = new LoginVO();
        vo.setToken(jwtUtil.generate(u.getId(), u.getRole()));
        vo.setId(u.getId());
        vo.setName(u.getUsername());
        vo.setRole(u.getRole());
        return vo;
    }
}
