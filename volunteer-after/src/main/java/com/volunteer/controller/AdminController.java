package com.volunteer.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.volunteer.common.Result;
import com.volunteer.dto.AttendanceDTO;
import com.volunteer.dto.AuditDTO;
import com.volunteer.entity.Activity;
import com.volunteer.entity.Registration;
import com.volunteer.entity.User;
import com.volunteer.mapper.ActivityMapper;
import com.volunteer.mapper.RegistrationMapper;
import com.volunteer.mapper.UserMapper;
import com.volunteer.service.ActivityService;
import com.volunteer.service.AttendanceService;
import com.volunteer.service.RegistrationService;
import com.volunteer.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private ActivityMapper activityMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private RegistrationMapper regMapper;
    @Autowired private ActivityService activityService;
    @Autowired private RegistrationService registrationService;
    @Autowired private AttendanceService attendanceService;

    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("MM-dd");
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /* ================= 看板 ================= */
    @GetMapping("/dashboard")
    public Result<?> dashboard(HttpServletRequest req) {
        Long uid = (Long) req.getAttribute("userId");
        String role = String.valueOf(req.getAttribute("role"));
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("role", role);
        boolean isOrg = "organizer".equals(role);
        LambdaQueryWrapper<Activity> actW = new LambdaQueryWrapper<>();
        if (isOrg) actW.eq(Activity::getOrganizerId, uid);
        m.put("totalActivities", activityMapper.selectCount(actW));
        LambdaQueryWrapper<Activity> pubW = new LambdaQueryWrapper<Activity>()
                .eq(Activity::getStatus, "published")
                .gt(Activity::getEndTime, LocalDateTime.now());
        if (isOrg) pubW.eq(Activity::getOrganizerId, uid);
        m.put("publishedActivities", activityMapper.selectCount(pubW));
        m.put("totalVolunteers", userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getRole, "volunteer")));
        m.put("organizers", userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getRole, "organizer")));
        m.put("admins", userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getRole, "admin")));
        m.put("totalHours", attendanceService.totalHours());
        m.put("totalSignups", regMapper.selectCount(null));

        // 组织者看到的是自己活动范围内的待办；管理员看到的是全站只读数字
        List<Map<String, Object>> pendingList = registrationService.listForAdmin("pending", uid, role);
        long pendingHours = registrationService.listForAdmin("approved", uid, role).stream()
                .filter(r -> !(Boolean) r.get("issued")).count();
        m.put("pendingAudit", pendingList.size());
        m.put("pendingHours", pendingHours);
        m.put("pendingUsers", pendingList.stream().map(r -> r.get("username")).distinct().limit(5).toList());

        m.put("lowCredit", userMapper.selectCount(new LambdaQueryWrapper<User>()
                .lt(User::getCreditScore, 85)));
        m.put("creditDist", creditDist());
        m.put("lowCreditUsers", lowCreditUsers());

        m.put("categoryStats", categoryStats(uid, isOrg));
        m.put("trend", last7DaysTrend());
        m.put("recent", recentRegistrations(uid, isOrg));
        return Result.success(m);
    }

    private List<Map<String, Object>> creditDist() {
        List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>()
                .eq(User::getRole, "volunteer"));
        Map<String, Long> bands = new LinkedHashMap<>();
        bands.put("95–100 优秀", 0L);
        bands.put("85–94 正常", 0L);
        bands.put("低于 85 需提示", 0L);
        for (User u : users) {
            int c = u.getCreditScore() == null ? 100 : u.getCreditScore();
            String k = c >= 95 ? "95–100 优秀" : c >= 85 ? "85–94 正常" : "低于 85 需提示";
            bands.merge(k, 1L, Long::sum);
        }
        List<Map<String, Object>> res = new ArrayList<>();
        bands.forEach((k, v) -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("name", k);
            m.put("count", v);
            res.add(m);
        });
        return res;
    }

    private List<Map<String, Object>> lowCreditUsers() {
        List<Map<String, Object>> res = new ArrayList<>();
        for (User u : userMapper.selectList(new LambdaQueryWrapper<User>()
                .lt(User::getCreditScore, UserService.CREDIT_FLOOR)
                .orderByAsc(User::getCreditScore))) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", u.getId());
            m.put("username", u.getUsername());
            m.put("nickname", u.getNickname() != null && !u.getNickname().isBlank() ? u.getNickname() : u.getUsername());
            m.put("creditScore", u.getCreditScore());
            m.put("role", u.getRole());
            res.add(m);
        }
        return res;
    }

    private List<Map<String, Object>> categoryStats(Long uid, boolean onlyMine) {
        LambdaQueryWrapper<Activity> w = new LambdaQueryWrapper<>();
        if (onlyMine) w.eq(Activity::getOrganizerId, uid);
        List<Activity> all = activityMapper.selectList(w);
        Map<String, Long> count = new LinkedHashMap<>();
        for (Activity a : all) {
            count.merge(a.getCategory() == null ? "未分类" : a.getCategory(), 1L, Long::sum);
        }
        List<Map<String, Object>> res = new ArrayList<>();
        count.entrySet().stream()
                .sorted((x, y) -> Long.compare(y.getValue(), x.getValue()))
                .forEach(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("name", e.getKey());
                    m.put("count", e.getValue());
                    res.add(m);
                });
        return res;
    }

    private List<Map<String, Object>> last7DaysTrend() {
        List<Map<String, Object>> res = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            Long c = regMapper.selectCount(new LambdaQueryWrapper<Registration>()
                    .ge(Registration::getSignupTime, day.atStartOfDay())
                    .lt(Registration::getSignupTime, day.plusDays(1).atStartOfDay()));
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("date", day.format(DAY));
            m.put("count", c == null ? 0 : c);
            res.add(m);
        }
        return res;
    }

    private List<Map<String, Object>> recentRegistrations(Long uid, boolean onlyMine) {
        List<Map<String, Object>> res = new ArrayList<>();
        List<Registration> list;
        if (onlyMine) {
            Set<Long> myAct = new HashSet<>();
            for (Activity a : activityMapper.selectList(new LambdaQueryWrapper<Activity>()
                    .eq(Activity::getOrganizerId, uid))) {
                myAct.add(a.getId());
            }
            if (myAct.isEmpty()) return res;
            list = regMapper.selectList(new LambdaQueryWrapper<Registration>()
                    .in(Registration::getActivityId, myAct)
                    .orderByDesc(Registration::getSignupTime)
                    .last("LIMIT 6"));
        } else {
            list = regMapper.selectList(new LambdaQueryWrapper<Registration>()
                    .orderByDesc(Registration::getSignupTime)
                    .last("LIMIT 6"));
        }
        for (Registration r : list) {
            User u = userMapper.selectById(r.getUserId());
            Activity a = activityMapper.selectById(r.getActivityId());
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", r.getId());
            m.put("username", u != null ? u.getUsername() : "已注销用户");
            m.put("activityTitle", a != null ? a.getTitle() : "活动已删除");
            m.put("status", r.getStatus());
            m.put("signupTime", r.getSignupTime() == null ? null : r.getSignupTime().format(FMT));
            res.add(m);
        }
        return res;
    }

    /* ================= 活动管理 ================= */
    @GetMapping("/activities")
    public Result<?> activities() {
        return Result.success(activityService.listAll());
    }

    @PostMapping("/activities")
    public Result<?> createActivity(@RequestBody Activity a, HttpServletRequest req) {
        activityService.create(a, (Long) req.getAttribute("userId"));
        return Result.success();
    }

    @PutMapping("/activities/{id}")
    public Result<?> updateActivity(@PathVariable Long id, @RequestBody Activity a) {
        a.setId(id);
        activityService.update(a);
        return Result.success();
    }

    @PatchMapping("/activities/{id}/status")
    public Result<?> changeStatus(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Object status = body.get("status");
        if (status == null) throw new RuntimeException("缺少 status 参数");
        activityService.changeStatus(id, String.valueOf(status));
        return Result.success();
    }

    @DeleteMapping("/activities/{id}")
    public Result<?> deleteActivity(@PathVariable Long id) {
        activityService.delete(id);
        return Result.success();
    }

    /* ================= 用户管理（仅管理员） ================= */
    @GetMapping("/users")
    public Result<?> users(HttpServletRequest req) {
        requireAdmin(req);
        List<User> list = userMapper.selectList(new LambdaQueryWrapper<User>().orderByAsc(User::getId));
        List<Map<String, Object>> res = new ArrayList<>();
        for (User u : list) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", u.getId());
            m.put("username", u.getUsername());
            m.put("nickname", u.getNickname() != null && !u.getNickname().isBlank() ? u.getNickname() : u.getUsername());
            m.put("phone", u.getPhone());
            m.put("role", u.getRole());
            m.put("skills", u.getSkills());
            m.put("creditScore", u.getCreditScore());
            m.put("avatar", u.getAvatar());
            m.put("createTime", u.getCreateTime() == null ? null : u.getCreateTime().format(FMT));
            res.add(m);
        }
        return Result.success(res);
    }

    @PatchMapping("/users/{id}")
    public Result<?> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> body,
                                HttpServletRequest req) {
        requireAdmin(req);
        User u = userMapper.selectById(id);
        if (u == null) throw new RuntimeException("用户不存在");
        Object role = body.get("role");
        if (role != null) {
            String r = String.valueOf(role);
            if (!Arrays.asList("volunteer", "organizer", "admin").contains(r)) {
                throw new RuntimeException("角色只能是 volunteer / organizer / admin");
            }
            u.setRole(r);
        }
        Object credit = body.get("creditScore");
        if (credit != null) {
            int score = ((Number) credit).intValue();
            if (score < 0 || score > 100) throw new RuntimeException("信用分需在 0–100 之间");
            u.setCreditScore(score);
        }
        userMapper.updateById(u);
        return Result.success();
    }

    /* ================= 管理员认证（注册为管理员需总管理员确认） ================= */
    @GetMapping("/pending-admins")
    public Result<?> pendingAdmins(HttpServletRequest req) {
        requireAdmin(req);
        List<User> list = userMapper.selectList(new LambdaQueryWrapper<User>()
                .eq(User::getRole, "admin_pending")
                .orderByDesc(User::getId));
        List<Map<String, Object>> res = new ArrayList<>();
        for (User u : list) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", u.getId());
            m.put("username", u.getUsername());
            m.put("phone", u.getPhone());
            m.put("createTime", u.getCreateTime() == null ? null : u.getCreateTime().format(FMT));
            res.add(m);
        }
        return Result.success(res);
    }

    @PostMapping("/pending-admins/{id}/approve")
    public Result<?> approveAdmin(@PathVariable Long id, HttpServletRequest req) {
        requireAdmin(req);
        User u = userMapper.selectById(id);
        if (u == null || !"admin_pending".equals(u.getRole())) throw new RuntimeException("该申请不存在或已处理");
        u.setRole("admin");
        userMapper.updateById(u);
        return Result.success();
    }

    @PostMapping("/pending-admins/{id}/reject")
    public Result<?> rejectAdmin(@PathVariable Long id, HttpServletRequest req) {
        requireAdmin(req);
        User u = userMapper.selectById(id);
        if (u == null || !"admin_pending".equals(u.getRole())) throw new RuntimeException("该申请不存在或已处理");
        userMapper.deleteById(id);
        return Result.success();
    }

    private void requireAdmin(HttpServletRequest req) {
        String role = String.valueOf(req.getAttribute("role"));
        if (!"admin".equals(role)) throw new RuntimeException("只有管理员可以进行用户管理");
    }

    /* ================= 报名审核 / 工时 ================= */
    @GetMapping("/registrations")
    public Result<?> registrations(@RequestParam(defaultValue = "all") String status,
                                   HttpServletRequest req) {
        Long uid = (Long) req.getAttribute("userId");
        String role = String.valueOf(req.getAttribute("role"));
        return Result.success(registrationService.listForAdmin(status, uid, role));
    }

    @PostMapping("/registrations/{id}/audit")
    public Result<?> audit(@PathVariable Long id, @RequestBody AuditDTO dto, HttpServletRequest req) {
        registrationService.audit(id, dto.getStatus(),
                (Long) req.getAttribute("userId"), String.valueOf(req.getAttribute("role")));
        return Result.success();
    }

    @PostMapping("/registrations/{id}/absent")
    public Result<?> absent(@PathVariable Long id, HttpServletRequest req) {
        registrationService.markAbsent(id,
                (Long) req.getAttribute("userId"), String.valueOf(req.getAttribute("role")));
        return Result.success();
    }

    @PostMapping("/attendance")
    public Result<?> issueHours(@RequestBody AttendanceDTO dto, HttpServletRequest req) {
        if (dto.getRegistrationId() == null) throw new RuntimeException("缺少报名记录 id");
        attendanceService.issue(dto.getRegistrationId(), dto.getHours(),
                (Long) req.getAttribute("userId"), String.valueOf(req.getAttribute("role")));
        return Result.success();
    }
}
