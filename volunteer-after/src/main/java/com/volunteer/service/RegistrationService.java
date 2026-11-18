package com.volunteer.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.volunteer.entity.Activity;
import com.volunteer.entity.Attendance;
import com.volunteer.entity.Registration;
import com.volunteer.entity.User;
import com.volunteer.mapper.ActivityMapper;
import com.volunteer.mapper.AttendanceMapper;
import com.volunteer.mapper.RegistrationMapper;
import com.volunteer.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationMapper regMapper;
    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AttendanceMapper attendanceMapper;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private String fmt(LocalDateTime t) {
        return t == null ? null : t.format(FMT);
    }

    @Transactional
    public Long signup(Long userId, Long activityId) {
        Activity a = activityMapper.selectById(activityId);
        if (a == null) throw new RuntimeException("活动不存在或已下架");
        if (!"published".equals(a.getStatus())) throw new RuntimeException("该活动暂未开放报名");
        if (a.getEndTime() != null && a.getEndTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("该活动已经结束");
        }
        if (regMapper.selectCount(new LambdaQueryWrapper<Registration>()
                .eq(Registration::getUserId, userId)
                .eq(Registration::getActivityId, activityId)) > 0) {
            throw new RuntimeException("你已经报名过该活动了");
        }
        // 条件更新：把“判满”与“自增”合成一条 SQL，避免并发超额
        int updated = activityMapper.update(null, new LambdaUpdateWrapper<Activity>()
                .eq(Activity::getId, activityId)
                .apply("signed_count < max_count")
                .setSql("signed_count = signed_count + 1"));
        if (updated == 0) throw new RuntimeException("名额已满，看看其他活动吧");

        Registration r = new Registration();
        r.setUserId(userId);
        r.setActivityId(activityId);
        r.setStatus("pending");
        regMapper.insert(r);
        return r.getId();
    }

    @Transactional
    public void cancel(Long userId, Long regId) {
        Registration r = regMapper.selectById(regId);
        if (r == null || !r.getUserId().equals(userId)) throw new RuntimeException("报名记录不存在");
        if (!"pending".equals(r.getStatus())) throw new RuntimeException("已审核的报名请联系组织者处理");
        regMapper.deleteById(regId);
        activityMapper.update(null, new LambdaUpdateWrapper<Activity>()
                .eq(Activity::getId, r.getActivityId())
                .apply("signed_count > 0")
                .setSql("signed_count = signed_count - 1"));
    }

    public List<Map<String, Object>> myList(Long userId) {
        List<Registration> list = regMapper.selectList(new LambdaQueryWrapper<Registration>()
                .eq(Registration::getUserId, userId)
                .orderByDesc(Registration::getSignupTime)
                .orderByDesc(Registration::getId));
        List<Map<String, Object>> res = new ArrayList<>();
        for (Registration r : list) {
            Activity a = activityMapper.selectById(r.getActivityId());
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", r.getId());
            m.put("activityId", r.getActivityId());
            m.put("activityTitle", a != null ? a.getTitle() : "活动已删除");
            m.put("category", a != null ? a.getCategory() : null);
            m.put("location", a != null ? a.getLocation() : null);
            m.put("startTime", a != null ? fmt(a.getStartTime()) : null);
            m.put("endTime", a != null ? fmt(a.getEndTime()) : null);
            m.put("hours", a != null ? a.getHours() : null);
            m.put("status", r.getStatus());
            m.put("signupTime", fmt(r.getSignupTime()));
            res.add(m);
        }
        return res;
    }

    /** 管理端报名列表，status 支持 all / pending / approved / rejected */
    public List<Map<String, Object>> listForAdmin(String status) {
        LambdaQueryWrapper<Registration> w = new LambdaQueryWrapper<Registration>()
                .orderByDesc(Registration::getSignupTime)
                .orderByDesc(Registration::getId);
        if (status != null && !"all".equals(status) && !status.isEmpty()) {
            w.eq(Registration::getStatus, status);
        }
        List<Registration> list = regMapper.selectList(w);
        Set<Long> issuedIds = new HashSet<>();
        for (Attendance t : attendanceMapper.selectList(null)) {
            if (t.getRegistrationId() != null) issuedIds.add(t.getRegistrationId());
        }
        List<Map<String, Object>> res = new ArrayList<>();
        for (Registration r : list) {
            User u = userMapper.selectById(r.getUserId());
            Activity a = activityMapper.selectById(r.getActivityId());
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", r.getId());
            m.put("userId", r.getUserId());
            m.put("username", u != null ? u.getUsername() : "用户已注销");
            m.put("phone", u != null ? u.getPhone() : null);
            m.put("skills", u != null ? u.getSkills() : null);
            m.put("activityId", r.getActivityId());
            m.put("activityTitle", a != null ? a.getTitle() : "活动已删除");
            m.put("category", a != null ? a.getCategory() : null);
            m.put("hours", a != null ? a.getHours() : null);
            m.put("startTime", a != null ? fmt(a.getStartTime()) : null);
            m.put("status", r.getStatus());
            m.put("signupTime", fmt(r.getSignupTime()));
            m.put("issued", issuedIds.contains(r.getId()));
            res.add(m);
        }
        return res;
    }

    @Transactional
    public void audit(Long regId, String status) {
        if (!"approved".equals(status) && !"rejected".equals(status)) {
            throw new RuntimeException("审核状态只能是 approved 或 rejected");
        }
        Registration r = regMapper.selectById(regId);
        if (r == null) throw new RuntimeException("报名记录不存在");
        if (!"pending".equals(r.getStatus())) throw new RuntimeException("该报名已处理过");

        r.setStatus(status);
        regMapper.updateById(r);

        // 驳回时释放名额
        if ("rejected".equals(status)) {
            activityMapper.update(null, new LambdaUpdateWrapper<Activity>()
                    .eq(Activity::getId, r.getActivityId())
                    .apply("signed_count > 0")
                    .setSql("signed_count = signed_count - 1"));
        }
    }
}
