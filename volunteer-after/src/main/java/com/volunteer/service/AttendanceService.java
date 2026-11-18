package com.volunteer.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.volunteer.entity.Attendance;
import com.volunteer.mapper.AttendanceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;

    /**
     * 统计某个志愿者累计确认的服务时长（小时）
     */
    public Double myHours(Long userId) {
        if (userId == null) {
            return 0.0;
        }
        List<Attendance> list = attendanceMapper.selectList(
                new LambdaQueryWrapper<Attendance>()
                        .eq(Attendance::getUserId, userId)
                        .eq(Attendance::getStatus, "confirmed")
        );
        return list.stream()
                .mapToDouble(a -> a.getHours() == null ? 0.0 : a.getHours())
                .sum();
    }
}
