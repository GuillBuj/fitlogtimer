package com.fitlogtimer.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitlogtimer.dto.display.CalendarItemDisplayDTO;
import com.fitlogtimer.service.WorkoutService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/calendar")
@AllArgsConstructor
public class CalendarApiController {

    private final WorkoutService workoutService;

    @GetMapping
    public List<CalendarItemDisplayDTO> getCalendarItems() {
        return workoutService.getCalendarItems();
    }
}