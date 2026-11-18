package com.volunteer.controller;

import com.volunteer.common.Result;
import com.volunteer.entity.Activity;
import com.volunteer.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {
    @Autowired private ActivityService activityService;

    @GetMapping
    public Result<List<Activity>> list() { return Result.success(activityService.list()); }

    @GetMapping("/{id}")
    public Result<Activity> detail(@PathVariable Long id) { return Result.success(activityService.detail(id)); }

    @PostMapping
    public Result<?> create(@RequestBody Activity a) {
        activityService.create(a);
        return Result.success();
    }
}