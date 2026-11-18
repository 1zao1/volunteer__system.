package com.volunteer.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.volunteer.entity.*;
import com.volunteer.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class RegistrationService {
    @Autowired private RegistrationMapper regMapper;
    @Autowired private ActivityMapper activityMapper;

    @Transactional
    public void signup(Long userId, Long activityId) {
        Activity a = activityMapper.selectById(activityId);
        if (a == null) throw new RuntimeException("活动不存在");
        if (a.getSignedCount() >= a.getMaxCount()) throw new RuntimeException("名额已满");
        if (regMapper.selectCount(new LambdaQueryWrapper<Registration>()
                .eq(Registration::getUserId, userId)
                .eq(Registration::getActivityId, activityId)) > 0)
            throw new RuntimeException("已报名");
        Registration r = new Registration();
        r.setUserId(userId); r.setActivityId(activityId); r.setStatus("pending");
        regMapper.insert(r);
        a.setSignedCount(a.getSignedCount() + 1);
        activityMapper.updateById(a);
    }

    public List<Map<String, Object>> myList(Long userId) {
        List<Registration> list = regMapper.selectList(new LambdaQueryWrapper<Registration>()
                .eq(Registration::getUserId, userId)
                .orderByDesc(Registration::getSignupTime));
        List<Map<String, Object>> res = new ArrayList<>();
        for (Registration r : list) {
            Activity a = activityMapper.selectById(r.getActivityId());
            Map<String, Object> m = new HashMap<>();
            m.put("id", r.getId());
            m.put("activityTitle", a != null ? a.getTitle() : "已删除");
            m.put("status", r.getStatus());
            m.put("signupTime", r.getSignupTime());
            res.add(m);
        }
        return res;
    }
}