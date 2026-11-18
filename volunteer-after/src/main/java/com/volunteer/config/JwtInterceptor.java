package com.volunteer.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.volunteer.common.JwtUtil;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) return true;

        String token = req.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) token = token.substring(7);

        try {
            Claims claims = jwtUtil.parse(token);
            req.setAttribute("userId", Long.valueOf(claims.getSubject()));
            Object role = claims.get("role");
            req.setAttribute("role", role);

            // 管理端接口只允许管理员与组织者访问
            String uri = req.getRequestURI();
            if (uri.startsWith("/api/admin")) {
                String r = String.valueOf(role);
                if (!"admin".equals(r) && !"organizer".equals(r)) {
                    resp.setStatus(403);
                    resp.setContentType("application/json;charset=UTF-8");
                    resp.getWriter().write("{\"code\":403,\"message\":\"没有权限访问管理端\"}");
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            resp.setStatus(401);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"code\":401,\"message\":\"登录已过期，请重新登录\"}");
            return false;
        }
    }
}
