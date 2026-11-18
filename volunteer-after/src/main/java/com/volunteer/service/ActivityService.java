package com.volunteer.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.volunteer.entity.Activity;
import com.volunteer.mapper.ActivityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ActivityService {
    @Autowired private ActivityMapper activityMapper;

    public List<Activity> list() {
        return activityMapper.selectList(new LambdaQueryWrapper<Activity>()
                .eq(Activity::getStatus, "published")
                .orderByDesc(Activity::getCreateTime));
    }
    public Activity detail(Long id) { return activityMapper.selectById(id); }
    public void create(Activity a) {
        a.setSignedCount(0); a.setStatus("published");
        activityMapper.insert(a);
    }
}