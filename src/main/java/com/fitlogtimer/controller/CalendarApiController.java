package com.fitlogtimer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitlogtimer.dto.display.CalendarDTO;
import com.fitlogtimer.service.WorkoutService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/calendar")
@AllArgsConstructor
@Slf4j
public class CalendarApiController {

    private final WorkoutService workoutService;

    @GetMapping
    public CalendarDTO getCalendar(){
        return workoutService.getCalendar();
    }
}