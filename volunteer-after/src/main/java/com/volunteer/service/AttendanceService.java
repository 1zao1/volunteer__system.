package com.volunteer.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.volunteer.entity.Activity;
import com.volunteer.entity.Attendance;
import com.volunteer.entity.Registration;
import com.volunteer.mapper.ActivityMapper;
import com.volunteer.mapper.AttendanceMapper;
import com.volunteer.mapper.RegistrationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceMapper attMapper;
    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private RegistrationMapper regMapper;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public List<Map<String, Object>> myHours(Long userId) {
        List<Attendance> list = attMapper.selectList(new LambdaQueryWrapper<Attendance>()
                .eq(Attendance::getUserId, userId)
                .orderByDesc(Attendance::getSignTime)
                .orderByDesc(Attendance::getId));
        List<Map<String, Object>> res = new ArrayList<>();
        for (Attendance a : list) {
            Activity act = activityMapper.selectById(a.getActivityId());
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", a.getId());
            m.put("activityTitle", act != null ? act.getTitle() : "活动已删除");
            m.put("category", act != null ? act.getCategory() : null);
            m.put("location", act != null ? act.getLocation() : null);
            m.put("hours", a.getHours());
            m.put("date", a.getSignTime() == null ? null : a.getSignTime().format(FMT));
            res.add(m);
        }
        return res;
    }

    @Transactional
    public void issue(Long registrationId, Integer hours) {
        Registration r = regMapper.selectById(registrationId);
        if (r == null) throw new RuntimeException("报名记录不存在");
        if (!"approved".equals(r.getStatus())) throw new RuntimeException("只有审核通过的报名才能认定工时");
        if (attMapper.selectCount(new LambdaQueryWrapper<Attendance>()
                .eq(Attendance::getRegistrationId, registrationId)) > 0) {
            throw new RuntimeException("该报名已经认定过工时");
        }
        Activity act = activityMapper.selectById(r.getActivityId());
        int h = (hours != null && hours > 0) ? hours : (act != null && act.getHours() != null ? act.getHours() : 0);
        if (h <= 0) throw new RuntimeException("工时必须大于 0");

        Attendance t = new Attendance();
        t.setRegistrationId(registrationId);
        t.setUserId(r.getUserId());
        t.setActivityId(r.getActivityId());
        t.setHours(h);
        t.setSignTime(LocalDateTime.now());
        attMapper.insert(t);
    }

    public long totalHours() {
        return attMapper.selectList(null).stream()
                .mapToLong(a -> a.getHours() == null ? 0 : a.getHours())
                .sum();
    }
}
